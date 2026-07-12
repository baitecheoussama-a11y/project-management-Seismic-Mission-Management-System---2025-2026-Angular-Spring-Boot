package com.pfe.webapp.service;

import com.pfe.webapp.dto.project.ProjectResponseDTO;
import com.pfe.webapp.dto.rapport.RapportRequestDTO;
import com.pfe.webapp.dto.rapport.RapportResponseDTO;
import com.pfe.webapp.dto.rendement.RendementResponseDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.mongodb.service.RapportDetailsService;
import com.pfe.webapp.repository.*;
import com.pfe.webapp.repository.rapport.RapportRepository;
import com.pfe.webapp.repository.rapport.RendementRepository;
import com.pfe.webapp.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RapportService {

    @Autowired
    private RapportRepository rapportRepository;

    @Autowired
    private RendementRepository rendementRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private AffectationEmployeRepository affectationEmployeRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RapportDetailsService rapportDetailsService;

    // ========== EXISTING METHODS ==========

    @Transactional(readOnly = true)
    public List<RapportResponseDTO> getRapportsByMission(Long missionId) {
        return rapportRepository.findByMissionIdOrderByDateDesc(missionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RapportResponseDTO> getRapportsForCurrentProject(Long missionId) {
        Project currentProject = projectRepository.findCurrentActiveProjectByMissionId(missionId)
                .orElse(null);

        if (currentProject == null) {
            return List.of();
        }

        return rapportRepository.findByProjectIdOrderByDateDesc(currentProject.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RapportResponseDTO addRapportToCurrentProject(RapportRequestDTO request, Long missionId) {
        Project currentProject = projectRepository.findCurrentActiveProjectByMissionId(missionId)
                .orElseThrow(() -> new RuntimeException("No active project found for this mission"));

        Rapport rapport = new Rapport();
        rapport.setTitre(request.getTitre());
        rapport.setDate(request.getDate());
        rapport.setResume(request.getResume());
        rapport.setProject(currentProject);

        Rapport saved = rapportRepository.save(rapport);

        // ✅ Send notification
        sendReportAddedNotification(saved, currentProject);

        return convertToDTO(saved);
    }

    // ✅ NEW: Get Rapport Entity (for internal use - MongoDB, etc.)
    @Transactional(readOnly = true)
    public Rapport getRapportEntityById(Long id) {
        return rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport not found with id: " + id));
    }

    // ✅ Get Rapport DTO (for API responses)
    @Transactional(readOnly = true)
    public RapportResponseDTO getRapportDTOById(Long id) {
        Rapport rapport = getRapportEntityById(id);
        return convertToDTO(rapport);
    }

    @Transactional
    public RapportResponseDTO updateRapport(Long id, RapportRequestDTO request) {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport not found with id: " + id));

        String oldTitle = rapport.getTitre();
        rapport.setTitre(request.getTitre());
        rapport.setDate(request.getDate());
        rapport.setResume(request.getResume());

        Rapport updated = rapportRepository.save(rapport);

        // ✅ Send notification for update
        sendReportUpdatedNotification(updated, oldTitle);

        return convertToDTO(updated);
    }

    @Transactional
    public void deleteRapport(Long id) {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport not found with id: " + id));

        String reportTitle = rapport.getTitre();
        Project project = rapport.getProject();

        List<Rendement> rendements = rendementRepository.findByRapportId(id);
        rendementRepository.deleteAll(rendements);
        rapportRepository.deleteById(id);

        // ✅ Send notification for deletion
        sendReportDeletedNotification(reportTitle, project);
    }

    // ✅ Keep both for compatibility
    @Transactional(readOnly = true)
    public RapportResponseDTO getRapportById(Long id) {
        return getRapportDTOById(id);
    }

    @Transactional(readOnly = true)
    public List<RapportResponseDTO> searchRapports(Long missionId, String keyword) {
        return rapportRepository.searchByKeyword(missionId, keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== NEW METHODS FOR REPORTS VIEWER ==========

    /**
     * Get all projects for current user's mission
     */
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByCurrentMission(UserDetails userDetails) {
        // Get current user's account
        Compte compte = compteRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Employe employe = compte.getEmploye();
        if (employe == null) {
            return List.of();
        }

        // Get current active mission for employee
        LocalDate currentDate = LocalDate.now();
        Optional<AffectationEmploye> activeAffectation = affectationEmployeRepository
                .findActiveMissionByEmployeId(employe.getId(), currentDate);

        if (activeAffectation.isEmpty()) {
            return List.of();
        }

        Mission mission = activeAffectation.get().getMission();
        Long missionId = mission.getId();

        // Get all projects for this mission
        List<Project> projects = projectRepository.findByMissionIdAndAnnuleFalse(missionId);

        // Convert to DTO
        return projects.stream()
                .map(this::convertProjectToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all reports by project ID
     */
    @Transactional(readOnly = true)
    public List<RapportResponseDTO> getReportsByProject(Long projectId) {
        List<Rapport> rapports = rapportRepository.findByProjectIdOrderByDateDesc(projectId);
        return rapports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search reports by keyword and optionally project ID
     */
    @Transactional(readOnly = true)
    public List<RapportResponseDTO> searchReports(String keyword, Long projectId) {
        List<Rapport> rapports;

        if (projectId != null) {
            // Search within specific project
            rapports = rapportRepository.searchByKeywordAndProject(keyword, projectId);
        } else {
            // Search across all accessible projects (based on user's mission)
            rapports = rapportRepository.searchByKeyword(keyword);
        }

        return rapports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== NOTIFICATION HELPERS ==========

    /**
     * Get all compte IDs that should receive notifications for a project
     */
    private List<Long> getRecipientCompteIdsForProject(Project project) {
        if (project == null || project.getMission() == null) {
            return List.of();
        }

        Long missionId = project.getMission().getId();

        // 1. Get all ADMIN and DIRECTEUR comptes
        List<Long> adminAndDirecteurIds = compteRepository.findComptesByRoleType(TypeRole.ADMINISTRATEUR)
                .stream()
                .map(Compte::getId)
                .collect(Collectors.toList());

        adminAndDirecteurIds.addAll(
                compteRepository.findComptesByRoleType(TypeRole.DIRECTEUR)
                        .stream()
                        .map(Compte::getId)
                        .collect(Collectors.toList())
        );

        // 2. Get CHEF_MISSION, CHEF_TERRAIN, Gestionnaire assigned to this mission
        List<Long> missionSpecificIds = affectationEmployeRepository
                .findByMissionIdAndDateFinAfterOrDateFinNull(missionId, LocalDate.now())
                .stream()
                .map(AffectationEmploye::getEmploye)
                .filter(employe -> employe.getCompte() != null)
                .map(employe -> employe.getCompte().getId())
                .collect(Collectors.toList());

        // Combine both lists
        adminAndDirecteurIds.addAll(missionSpecificIds);

        return adminAndDirecteurIds.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Get the correct link based on user role
     * - ADMINISTRATEUR, DIRECTEUR -> /pages/tables/reports
     * - CHEF_MISSION, CHEF_TERRAIN, Gestionnaire -> /pages/safety/rapports
     */
    private String getReportLink(Project project, Long actorCompteId) {
        if (actorCompteId == null) {
            return "/pages/safety/rapports";
        }

        Optional<Compte> compte = compteRepository.findById(actorCompteId);
        if (compte.isEmpty()) {
            return "/pages/safety/rapports";
        }

        // Check if user has ADMIN or DIRECTEUR role
        boolean isAdminOrDirecteur = compte.get().getRoles().stream()
                .map(AffectationRole::getRole)
                .anyMatch(role -> role.getType() == TypeRole.ADMINISTRATEUR ||
                        role.getType() == TypeRole.DIRECTEUR);

        if (isAdminOrDirecteur) {
            return "/pages/tables/reports";
        } else {
            return "/pages/safety/rapports";
        }
    }

    private String getProjectLink(Long projectId) {
        return "/pages/project-overview/" + projectId;
    }

    private void sendReportAddedNotification(Rapport rapport, Project project) {
        String title = "New Report Added";
        String message = String.format("Report '%s' has been added to project '%s'",
                rapport.getTitre(),
                project.getNom());

        String link = "/pages/safety/rapports";

        List<Long> recipientIds = getRecipientCompteIdsForProject(project);

        if (!recipientIds.isEmpty()) {
            notificationService.createNotificationForUsers(
                    recipientIds,
                    title,
                    message,
                    NotificationType.REPORT_ADDED,
                    link
            );
        }
    }

    private void sendReportUpdatedNotification(Rapport rapport, String oldTitle) {
        Project project = rapport.getProject();
        if (project == null) return;

        String title = "Report Updated";
        String message = String.format("Report '%s' (was '%s') has been updated in project '%s'",
                rapport.getTitre(),
                oldTitle,
                project.getNom());
        String link = "/pages/safety/rapports";

        List<Long> recipientIds = getRecipientCompteIdsForProject(project);

        if (!recipientIds.isEmpty()) {
            notificationService.createNotificationForUsers(
                    recipientIds,
                    title,
                    message,
                    NotificationType.REPORT_ADDED,
                    link
            );
        }
    }

    private void sendReportDeletedNotification(String reportTitle, Project project) {
        if (project == null) return;

        String title = "Report Deleted";
        String message = String.format("Report '%s' has been deleted from project '%s'",
                reportTitle,
                project.getNom());
        String link = "/pages/safety/rapports";

        List<Long> recipientIds = getRecipientCompteIdsForProject(project);

        if (!recipientIds.isEmpty()) {
            notificationService.createNotificationForUsers(
                    recipientIds,
                    title,
                    message,
                    NotificationType.REPORT_ADDED,
                    link
            );
        }
    }

    // ========== CONVERTER METHODS ==========

    private ProjectResponseDTO convertProjectToDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setNom(project.getNom());
        dto.setDescription(project.getDescription());
        dto.setBudget(project.getBudget());
        dto.setObjectifVP(project.getObjectifVP());
        dto.setObjectifDebut(project.getObjectifDebut());
        dto.setObjectifFin(project.getObjectifFin());
        dto.setProgression(project.getProgression() != null ? project.getProgression() : project.calculateProgress());

        if (project.getMission() != null) {
            dto.setMissionId(project.getMission().getId());
            dto.setMissionCode(project.getMission().getCodeMission());
        }

        return dto;
    }

    private RapportResponseDTO convertToDTO(Rapport rapport) {
        String projectName = rapport.getProject() != null ? rapport.getProject().getNom() : null;
        String missionCode = rapport.getProject() != null && rapport.getProject().getMission() != null
                ? rapport.getProject().getMission().getCodeMission() : null;

        return new RapportResponseDTO(
                rapport.getId(),
                rapport.getTitre(),
                rapport.getDate(),
                rapport.getResume(),
                rapport.getProject() != null ? rapport.getProject().getId() : null,
                projectName,
                missionCode
        );
    }

    private RapportResponseDTO convertToDTOWithRendements(Rapport rapport) {
        RapportResponseDTO dto = convertToDTO(rapport);

        List<RendementResponseDTO> rendements = rendementRepository.findByRapportId(rapport.getId())
                .stream()
                .map(this::convertRendementToDTO)
                .collect(Collectors.toList());

        dto.setRendements(rendements);
        return dto;
    }

    private RendementResponseDTO convertRendementToDTO(Rendement rendement) {
        double dureeHeures = 0;
        if (rendement.getHeureDebut() != null && rendement.getHeureFin() != null) {
            java.time.Duration duration = java.time.Duration.between(rendement.getHeureDebut(), rendement.getHeureFin());
            dureeHeures = duration.toMinutes() / 60.0;
        }

        Long activeId = null;
        if (rendement.getAffectationEquipe() != null && rendement.getAffectationEquipe().getActive() != null) {
            activeId = rendement.getAffectationEquipe().getActive().getId();
        }

        return new RendementResponseDTO(
                rendement.getId(),
                rendement.getHeureDebut(),
                rendement.getHeureFin(),
                rendement.getValeurRendement(),
                rendement.getUniteRendement(),
                rendement.getDate(),
                dureeHeures,
                rendement.getRapport() != null ? rendement.getRapport().getId() : null,
                rendement.getAffectationEquipe() != null ? rendement.getAffectationEquipe().getId() : null,
                activeId
        );
    }
}