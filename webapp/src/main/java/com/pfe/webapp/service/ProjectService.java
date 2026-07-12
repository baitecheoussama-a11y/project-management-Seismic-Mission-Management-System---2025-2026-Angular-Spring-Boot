package com.pfe.webapp.service;

import com.pfe.webapp.dto.av.AvancementDTO;
import com.pfe.webapp.dto.project.*;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.AffectationEmployeRepository;
import com.pfe.webapp.repository.CompteRepository;
import com.pfe.webapp.repository.MissionRepository;
import com.pfe.webapp.repository.ProjectRepository;
import com.pfe.webapp.repository.rapport.RapportRepository;
import com.pfe.webapp.service.notification.NotificationService;
import com.pfe.webapp.service.project.ProjectProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private RapportRepository rapportRepository;

    @Autowired
    private AffectationEmployeRepository affectationEmployeRepository;

    @Autowired
    private ProjectProgressService projectProgressService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CompteRepository compteRepository;

    // ============ HELPER METHOD FOR LINK ============

    private String getProjectLink(Long projectId) {
        return "/pages/pages/tables/reports" + projectId;
    }

    // ============ GET PROJECTS ============

    @Transactional(readOnly = true)
    public ProjectResponseDTO getCurrentProjectByMission(Long missionId) {
        Optional<Project> project = projectRepository.findCurrentActiveProjectByMissionId(missionId);
        if (project.isPresent()) {
            return convertToResponseDTO(project.get());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        ProjectResponseDTO dto = convertToResponseDTO(project);

        // Add rapports
        List<RapportResponseDTO> rapports = rapportRepository.findByProjectId(id).stream()
                .map(r -> new RapportResponseDTO(
                        r.getId(),
                        r.getTitre(),
                        r.getDate(),
                        r.getResume(),
                        r.getProject().getId()
                ))
                .collect(Collectors.toList());
        dto.setRapports(rapports);

        return dto;
    }

    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO request) {
        // Check if mission has any project that is still active
        boolean hasActiveProject = projectRepository.existsActiveProjectByMissionId(request.getMissionId());

        if (hasActiveProject) {
            throw new RuntimeException("This mission already has an active project. " +
                    "Complete or cancel the current project before creating a new one.");
        }

        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + request.getMissionId()));

        Project project = new Project();
        project.setNom(request.getNom());
        project.setDescription(request.getDescription());
        project.setBudget(request.getBudget());
        project.setObjectifVP(request.getObjectifVP());
        project.setObjectifDebut(request.getObjectifDebut());
        project.setObjectifFin(request.getObjectifFin());
        project.setProgression(0);
        project.setAnnule(false);
        project.setDateStartReelle(null);
        project.setDateFinReelle(null);
        project.setMission(mission);

        Project saved = projectRepository.save(project);

        // ✅ Send notifications
        sendProjectCreationNotifications(saved);

        return convertToResponseDTO(saved);
    }

    @Transactional
    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        String oldName = project.getNom();
        project.setNom(request.getNom());
        project.setDescription(request.getDescription());
        project.setBudget(request.getBudget());
        project.setObjectifVP(request.getObjectifVP());
        project.setObjectifDebut(request.getObjectifDebut());
        project.setObjectifFin(request.getObjectifFin());

        Project updated = projectRepository.save(project);

        // ✅ Send notifications
        sendProjectUpdateNotifications(updated, oldName);

        return convertToResponseDTO(updated);
    }

    // ============ UPDATE REAL DATES ============

    @Transactional
    public ProjectResponseDTO updateProjectRealDates(Long projectId, LocalDate dateStartReelle, LocalDate dateFinReelle) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        String oldStatus = project.calculateStatus();
        LocalDate oldStartReelle = project.getDateStartReelle();
        LocalDate oldFinReelle = project.getDateFinReelle();

        if (dateStartReelle != null) {
            project.setDateStartReelle(dateStartReelle);
        }
        if (dateFinReelle != null) {
            project.setDateFinReelle(dateFinReelle);
        }

        // Auto-update annule flag based on status
        String status = project.calculateStatus();
        if ("ANNULE".equals(status)) {
            project.setAnnule(true);
        } else if ("TERMINI".equals(status)) {
            project.setAnnule(false);
        }

        // Update progression based on status
        int progression = project.calculateProgressionFromStatus();
        project.setProgression(progression);

        Project updated = projectRepository.save(project);

        // ✅ Send notifications based on status change
        if (!oldStatus.equals(status)) {
            sendProjectStatusChangeNotifications(updated, oldStatus, status);
        }

        // ✅ Send notifications for date changes
        if (dateStartReelle != null && !dateStartReelle.equals(oldStartReelle)) {
            sendProjectDateUpdateNotifications(updated, "start", dateStartReelle);
        }
        if (dateFinReelle != null && !dateFinReelle.equals(oldFinReelle)) {
            sendProjectDateUpdateNotifications(updated, "end", dateFinReelle);
        }

        return convertToResponseDTO(updated);
    }

    // ============ CANCEL PROJECT ============

    @Transactional
    public ProjectResponseDTO cancelProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        // Check if project is already completed or cancelled
        if (Boolean.TRUE.equals(project.getAnnule())) {
            throw new RuntimeException("Project is already cancelled");
        }

        if ("TERMINI".equals(project.calculateStatus())) {
            throw new RuntimeException("Cannot cancel a completed project");
        }

        String oldStatus = project.calculateStatus();

        // Set annule to true
        project.setAnnule(true);

        // Set actual end date to now if not already set
        if (project.getDateFinReelle() == null) {
            project.setDateFinReelle(LocalDate.now());
        }

        // Set progression to 0 for cancelled projects
        project.setProgression(0);

        Project saved = projectRepository.save(project);

        // ✅ Send cancellation notifications
        sendProjectCancellationNotifications(saved, oldStatus);

        return convertToResponseDTO(saved);
    }

    // ============ PROJECT PROGRESS STATS ============

    public ProjectProgressStatsDTO getProjectProgressStats(Long projectId) {
        return projectProgressService.getProjectProgressStats(projectId);
    }

    public List<ActivityProgressDTO> getProjectActivitiesProgress(Long projectId) {
        return projectProgressService.getActivitiesByProject(projectId);
    }

    public void updateProjectProgressionFromActivities(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        int progressionFromActivities = projectProgressService.calculateProjectProgressionFromActivities(projectId);

        project.setProgression(progressionFromActivities);
        projectRepository.save(project);
    }

    // ============ RAPPORT MANAGEMENT ============

    @Transactional
    public RapportResponseDTO addRapport(Long projectId, RapportRequestDTO request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        Rapport rapport = new Rapport();
        rapport.setTitre(request.getTitre());
        rapport.setDate(request.getDate());
        rapport.setResume(request.getResume());
        rapport.setProject(project);

        Rapport saved = rapportRepository.save(rapport);

        // ✅ Send report added notification
        sendReportAddedNotifications(project, saved);

        return new RapportResponseDTO(
                saved.getId(),
                saved.getTitre(),
                saved.getDate(),
                saved.getResume(),
                saved.getProject().getId()
        );
    }

    @Transactional(readOnly = true)
    public List<RapportResponseDTO> getRapportsByProjectId(Long projectId) {
        return rapportRepository.findByProjectId(projectId).stream()
                .map(r -> new RapportResponseDTO(
                        r.getId(),
                        r.getTitre(),
                        r.getDate(),
                        r.getResume(),
                        r.getProject().getId()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RapportResponseDTO getRapportById(Long id) {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport not found with id: " + id));

        return new RapportResponseDTO(
                rapport.getId(),
                rapport.getTitre(),
                rapport.getDate(),
                rapport.getResume(),
                rapport.getProject().getId()
        );
    }

    @Transactional
    public RapportResponseDTO updateRapport(Long id, RapportRequestDTO request) {
        Rapport rapport = rapportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapport not found with id: " + id));

        rapport.setTitre(request.getTitre());
        rapport.setDate(request.getDate());
        rapport.setResume(request.getResume());

        Rapport updated = rapportRepository.save(rapport);

        return new RapportResponseDTO(
                updated.getId(),
                updated.getTitre(),
                updated.getDate(),
                updated.getResume(),
                updated.getProject().getId()
        );
    }

    @Transactional
    public void deleteRapport(Long id) {
        if (!rapportRepository.existsById(id)) {
            throw new RuntimeException("Rapport not found with id: " + id);
        }
        rapportRepository.deleteById(id);
    }

    // ============ OTHER PROJECT METHODS ============

    @Transactional(readOnly = true)
    public ProjectResponseDTO getActiveProjectByMissionId(Long missionId) {
        Optional<Project> project = projectRepository.findCurrentActiveProjectByMissionId(missionId);
        if (project.isPresent()) {
            return convertToResponseDTO(project.get());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectForEmployee(Long employeId) {
        LocalDate currentDate = LocalDate.now();
        Optional<AffectationEmploye> activeAffectation = affectationEmployeRepository
                .findActiveMissionByEmployeId(employeId, currentDate);

        if (activeAffectation.isEmpty()) {
            return null;
        }

        Mission mission = activeAffectation.get().getMission();
        return getActiveProjectByMissionId(mission.getId());
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByCurrentMission(Long employeId) {
        LocalDate currentDate = LocalDate.now();
        Optional<AffectationEmploye> activeAffectation = affectationEmployeRepository
                .findActiveMissionByEmployeId(employeId, currentDate);

        if (activeAffectation.isEmpty()) {
            return List.of();
        }

        Mission mission = activeAffectation.get().getMission();
        return projectRepository.findAllProjectsByMissionId(mission.getId()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByMissionId(Long missionId) {
        return projectRepository.findByMissionId(missionId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getAccessibleProjects(Long employeId) {
        LocalDate currentDate = LocalDate.now();
        Optional<AffectationEmploye> activeAffectation = affectationEmployeRepository
                .findActiveMissionByEmployeId(employeId, currentDate);

        if (activeAffectation.isEmpty()) {
            return List.of();
        }

        Mission mission = activeAffectation.get().getMission();
        return projectRepository.findByMissionId(mission.getId()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectWithMissionDTO getProjectWithMission(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
        return convertToProjectWithMissionDTO(project);
    }

    // ============ NOTIFICATION HELPERS ============

    /**
     * Get all compte IDs that should receive notifications for a mission
     * - ADMIN and DIRECTEUR: Always receive notifications (can see all missions)
     * - CHEF_MISSION, CHEF_TERRAIN, Gestionnaire: Only if they are assigned to the mission
     */
    private List<Long> getRecipientCompteIdsForMission(Long missionId) {
        // 1. Get all ADMIN and DIRECTEUR comptes (they see everything)
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
                .filter(employe -> employe.getCompte() != null) // Only employees with accounts
                .map(employe -> employe.getCompte().getId())
                .collect(Collectors.toList());

        // Combine both lists
        adminAndDirecteurIds.addAll(missionSpecificIds);

        // Return unique IDs
        return adminAndDirecteurIds.stream().distinct().collect(Collectors.toList());
    }

    private void sendProjectCreationNotifications(Project project) {
        String title = "New Project Created";
        String message = String.format("Project '%s' has been created for mission %s",
                project.getNom(),
                project.getMission().getCodeMission());
        String link = getProjectLink(project.getId());

        List<Long> recipientIds = getRecipientCompteIdsForMission(project.getMission().getId());
        notificationService.createNotificationForUsers(
                recipientIds,
                title,
                message,
                NotificationType.PROJECT_CREATED,
                link
        );
    }

    private void sendProjectUpdateNotifications(Project project, String oldName) {
        String title = "Project Updated";
        String message = String.format("Project '%s' (was '%s') has been updated for mission %s",
                project.getNom(),
                oldName,
                project.getMission().getCodeMission());
        String link = getProjectLink(project.getId());

        List<Long> recipientIds = getRecipientCompteIdsForMission(project.getMission().getId());
        notificationService.createNotificationForUsers(
                recipientIds,
                title,
                message,
                NotificationType.PROJECT_UPDATED,
                link
        );
    }

    private void sendProjectStatusChangeNotifications(Project project, String oldStatus, String newStatus) {
        String title = "Project Status Changed";
        String message = String.format("Project '%s' status changed from %s to %s",
                project.getNom(),
                oldStatus,
                newStatus);
        String link = getProjectLink(project.getId());

        List<Long> recipientIds = getRecipientCompteIdsForMission(project.getMission().getId());
        notificationService.createNotificationForUsers(
                recipientIds,
                title,
                message,
                NotificationType.PROJECT_UPDATED,
                link
        );
    }

    private void sendProjectCancellationNotifications(Project project, String oldStatus) {
        String title = "Project Cancelled";
        String message = String.format("Project '%s' has been cancelled (was %s)",
                project.getNom(),
                oldStatus);
        String link = getProjectLink(project.getId());

        List<Long> recipientIds = getRecipientCompteIdsForMission(project.getMission().getId());
        notificationService.createNotificationForUsers(
                recipientIds,
                title,
                message,
                NotificationType.PROJECT_CANCELLED,
                link
        );
    }

    private void sendProjectDateUpdateNotifications(Project project, String dateType, LocalDate date) {
        String title = "Project Date Updated";
        String dateLabel = dateType.equals("start") ? "Start" : "End";
        String message = String.format("Project '%s' %s date updated to %s",
                project.getNom(),
                dateLabel,
                date.toString());
        String link = getProjectLink(project.getId());

        List<Long> recipientIds = getRecipientCompteIdsForMission(project.getMission().getId());
        notificationService.createNotificationForUsers(
                recipientIds,
                title,
                message,
                NotificationType.PROJECT_UPDATED,
                link
        );
    }

    private void sendReportAddedNotifications(Project project, Rapport rapport) {
        String title = "New Report Added";
        String message = String.format("Report '%s' has been added to project '%s'",
                rapport.getTitre(),
                project.getNom());
        String link = getProjectLink(project.getId()) + "/reports";

        List<Long> recipientIds = getRecipientCompteIdsForMission(project.getMission().getId());
        notificationService.createNotificationForUsers(
                recipientIds,
                title,
                message,
                NotificationType.REPORT_ADDED,
                link
        );
    }

    // ============ CONVERTERS ============

    private ProjectResponseDTO convertToResponseDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setNom(project.getNom());
        dto.setDescription(project.getDescription());
        dto.setBudget(project.getBudget());
        dto.setObjectifVP(project.getObjectifVP());
        dto.setObjectifDebut(project.getObjectifDebut());
        dto.setObjectifFin(project.getObjectifFin());
        dto.setAnnule(project.getAnnule());

        // Use calculated status-based progression
        int progression = project.getProgression() != null ? project.getProgression() : project.calculateProgress();
        dto.setProgression(progression);

        // Set real dates
        dto.setDateStartReelle(project.getDateStartReelle());
        dto.setDateFinReelle(project.getDateFinReelle());

        // Set status (calculated from dates)
        dto.setStatus(project.calculateStatus());

        if (project.getMission() != null) {
            dto.setMissionId(project.getMission().getId());
            dto.setMissionCode(project.getMission().getCodeMission());
        }

        return dto;
    }

    private ProjectWithMissionDTO convertToProjectWithMissionDTO(Project project) {
        ProjectWithMissionDTO dto = new ProjectWithMissionDTO();

        dto.setId(project.getId());
        dto.setNom(project.getNom());
        dto.setDescription(project.getDescription());
        dto.setBudget(project.getBudget());
        dto.setObjectifVP(project.getObjectifVP());
        dto.setObjectifDebut(project.getObjectifDebut());
        dto.setObjectifFin(project.getObjectifFin());
        dto.setProgression(project.getProgression() != null ? project.getProgression() : project.calculateProgress());
        dto.setAnnule(project.getAnnule());
        dto.setDateStartReelle(project.getDateStartReelle());
        dto.setDateFinReelle(project.getDateFinReelle());
        dto.setStatus(project.calculateStatus());

        if (project.getMission() != null) {
            dto.setMissionId(project.getMission().getId());
            dto.setMissionCode(project.getMission().getCodeMission());
            dto.setMissionName("Mission " + project.getMission().getCodeMission());
        }

        return dto;
    }
}