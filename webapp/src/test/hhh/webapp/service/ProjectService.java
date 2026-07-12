// service/ProjectService.java
package com.pfe.webapp.service;

import com.pfe.webapp.dto.av.AvancementDTO;
import com.pfe.webapp.dto.av.EtatAvancementDTO;
import com.pfe.webapp.dto.project.*;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.AffectationEmployeRepository;
import com.pfe.webapp.repository.MissionRepository;
import com.pfe.webapp.repository.ProjectRepository;
import com.pfe.webapp.repository.rapport.RapportRepository;
import com.pfe.webapp.service.av.EtatAvancementService;
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
    private EtatAvancementService etatAvancementService;

    @Transactional(readOnly = true)
    public ProjectResponseDTO getCurrentProjectByMission(Long missionId) {
        Optional<Project> project = projectRepository.findCurrentActiveProjectByMissionId(missionId);
        if (project.isPresent()) {
            return convertToResponseDTO(project.get());
        }
        return null;
    }

    // In ProjectService.java - update getProjectById
// In ProjectService.java - add this method
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        // Check and update delay status when loading the project
        project.checkAndUpdateDelayStatus();

        // Save if status was updated
        if (project.getEtatAvancements() != null && !project.getEtatAvancements().isEmpty()) {
            Optional<EtatAvancement> projectStatus = project.getEtatAvancements().stream()
                    .filter(e -> e.getActive() == null)
                    .findFirst();
            if (projectStatus.isPresent() &&
                    projectStatus.get().getStatus() == StatusEtatAvancement.ENRETARD) {
                // Status was updated, save it
                etatAvancementService.updateEtatAvancementStatus(
                        projectStatus.get().getId(),
                        "ENRETARD"
                );
            }
        }

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

        // Add etatAvancements (project status)
        List<EtatAvancementDTO> etatAvancements = etatAvancementService.getEtatAvancementsByProject(id);
        dto.setEtatAvancements(etatAvancements);

        return dto;
    }


    @Transactional
    public ProjectResponseDTO createProject(ProjectRequestDTO request) {
        // Check if mission has any project that is still active
        // A project is considered active if it's not cancelled AND its status is not TERMINI or ANNULE
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
        project.setMission(mission);

        Project saved = projectRepository.save(project);

        // ✅ AUTO-CREATE EtatAvancement for the project
        etatAvancementService.createEtatAvancementForProject(saved.getId());

        return convertToResponseDTO(saved);
    }

    @Transactional
    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        project.setNom(request.getNom());
        project.setDescription(request.getDescription());
        project.setBudget(request.getBudget());
        project.setObjectifVP(request.getObjectifVP());
        project.setObjectifDebut(request.getObjectifDebut());
        project.setObjectifFin(request.getObjectifFin());

        Project updated = projectRepository.save(project);
        return convertToResponseDTO(updated);
    }

    private ProjectResponseDTO convertToResponseDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setNom(project.getNom());
        dto.setDescription(project.getDescription());
        dto.setBudget(project.getBudget());
        dto.setObjectifVP(project.getObjectifVP());
        dto.setObjectifDebut(project.getObjectifDebut());
        dto.setObjectifFin(project.getObjectifFin());
        dto.setProgression(project.getProgression() != null ? project.getProgression() : project.calculateProgress());
        dto.setDateFinReelle(project.getDateFinReelle());

        if (project.getMission() != null) {
            dto.setMissionId(project.getMission().getId());
            dto.setMissionCode(project.getMission().getCodeMission());
        }

        // ✅ Add etatAvancements (project status) to the response
        if (project.getEtatAvancements() != null && !project.getEtatAvancements().isEmpty()) {
            List<EtatAvancementDTO> etatDTOs = project.getEtatAvancements().stream()
                    .filter(e -> e.getActive() == null) // Only project status (active is null)
                    .map(this::convertEtatToDTO)
                    .collect(Collectors.toList());
            dto.setEtatAvancements(etatDTOs);
        }

        return dto;
    }

    // Add this helper method to convert EtatAvancement to DTO
// Add this helper method to convert EtatAvancement to DTO
    private EtatAvancementDTO convertEtatToDTO(EtatAvancement etat) {
        EtatAvancementDTO dto = new EtatAvancementDTO();
        dto.setId(etat.getId());
        // ✅ Use the enum directly, not the string name
        dto.setStatus(etat.getStatus()); // This is already StatusEtatAvancement enum
        dto.setDateLastAvancement(etat.getDateLastAvancement());
        dto.setProjectId(etat.getProject() != null ? etat.getProject().getId() : null);
        dto.setProjectName(etat.getProject() != null ? etat.getProject().getNom() : null);

        if (etat.getActive() != null) {
            dto.setActiveId(etat.getActive().getId());
            dto.setActiveCode(etat.getActive().getCodeActive());
        }

        // Add avancements (progress updates) if needed
        if (etat.getAvancements() != null && !etat.getAvancements().isEmpty()) {
            List<AvancementDTO> avancementDTOs = etat.getAvancements().stream()
                    .map(this::convertAvancementToDTO)
                    .collect(Collectors.toList());
            dto.setAvancements(avancementDTOs);
        }

        return dto;
    }
    // Add helper method to convert Avancement to DTO
    private AvancementDTO convertAvancementToDTO(Avancement avancement) {
        AvancementDTO dto = new AvancementDTO();
        dto.setId(avancement.getId());
        dto.setTitre(avancement.getTitre());
        dto.setDate(avancement.getDate());
        dto.setResume(avancement.getResume());
        dto.setEtatAvancementId(avancement.getEtatAvancement() != null ? avancement.getEtatAvancement().getId() : null);
        return dto;
    }

    // service/ProjectService.java - أضف هذه الدوال

    // Get active project for a mission
    // Get active project for a mission (not completed or cancelled)
    public ProjectResponseDTO getActiveProjectByMissionId(Long missionId) {
        // Use the same logic as getCurrentProjectByMission
        Optional<Project> project = projectRepository.findCurrentActiveProjectByMissionId(missionId);
        if (project.isPresent()) {
            return convertToResponseDTO(project.get());
        }
        return null;
    }

    // Get project by mission and employee (through mission)
    public ProjectResponseDTO getProjectForEmployee(Long employeId) {
        // First get the employee's active mission
        LocalDate currentDate = LocalDate.now();
        Optional<AffectationEmploye> activeAffectation = affectationEmployeRepository
                .findActiveMissionByEmployeId(employeId, currentDate);

        if (activeAffectation.isEmpty()) {
            return null;
        }

        Mission mission = activeAffectation.get().getMission();
        return getActiveProjectByMissionId(mission.getId());
    }

    // Add rapport to project
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

        return new RapportResponseDTO(
                saved.getId(),
                saved.getTitre(),
                saved.getDate(),
                saved.getResume(),
                saved.getProject().getId()
        );
    }

    // Get all rapports by project
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

    // Get rapport by ID
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

    // Update rapport
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

    // Delete rapport
    @Transactional
    public void deleteRapport(Long id) {
        if (!rapportRepository.existsById(id)) {
            throw new RuntimeException("Rapport not found with id: " + id);
        }
        rapportRepository.deleteById(id);
    }

    // service/ProjectService.java - أضف هذه الدوال

    // Get all projects for current employee's mission
    // service/ProjectService.java - Update this method
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByCurrentMission(Long employeId) {
        // First get the employee's current mission
        LocalDate currentDate = LocalDate.now();
        Optional<AffectationEmploye> activeAffectation = affectationEmployeRepository
                .findActiveMissionByEmployeId(employeId, currentDate);

        if (activeAffectation.isEmpty()) {
            return List.of(); // Return empty list if no active mission
        }

        Mission mission = activeAffectation.get().getMission();

        // ✅ Fetch projects with their etatAvancements
        return projectRepository.findAllProjectsByMissionId(mission.getId()).stream()
                .map(this::convertToResponseDTO)  // This will now include etatAvancements
                .collect(Collectors.toList());
    }

    // أضف هذه الدوال في ProjectService.java الموجود

    @Autowired
    private ProjectProgressService projectProgressService;

    public ProjectProgressStatsDTO getProjectProgressStats(Long projectId) {
        return projectProgressService.getProjectProgressStats(projectId);
    }


    public List<ActivityProgressDTO> getProjectActivitiesProgress(Long projectId) {
        return projectProgressService.getActivitiesByProject(projectId);
    }

    // In ProjectService.java - add this method
    @Transactional(readOnly = true)
    public ProjectWithMissionDTO getProjectWithMission(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        return convertToProjectWithMissionDTO(project);
    }

    // Add this conversion method
    private ProjectWithMissionDTO convertToProjectWithMissionDTO(Project project) {
        ProjectWithMissionDTO dto = new ProjectWithMissionDTO();

        // Basic project info
        dto.setId(project.getId());
        dto.setNom(project.getNom());
        dto.setDescription(project.getDescription());
        dto.setBudget(project.getBudget());
        dto.setObjectifVP(project.getObjectifVP());
        dto.setObjectifDebut(project.getObjectifDebut());
        dto.setObjectifFin(project.getObjectifFin());
        dto.setProgression(project.getProgression() != null ? project.getProgression() : project.calculateProgress());
        dto.setAnnule(project.getAnnule());

        // ✅ Mission data (not ignored)
        if (project.getMission() != null) {
            dto.setMissionId(project.getMission().getId());
            dto.setMissionCode(project.getMission().getCodeMission());
            dto.setMissionName("Mission " + project.getMission().getCodeMission());
        }

        return dto;
    }

    // In ProjectService.java - Add this method



    /**
     * Update project progression based on activities completion
     */
    @Transactional
    public void updateProjectProgressionFromActivities(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        int progressionFromActivities = projectProgressService.calculateProjectProgressionFromActivities(projectId);

        project.setProgression(progressionFromActivities);
        projectRepository.save(project);
    }

    // أضف هذه الدوال في ProjectService.java

    // Get all projects (for ADMIN/DIRECTEUR)
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Get projects by mission ID (for ADMIN/DIRECTEUR)
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getProjectsByMissionId(Long missionId) {
        return projectRepository.findByMissionId(missionId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Get accessible projects based on user role
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getAccessibleProjects(Long employeId) {
        // Check if user has admin/directeur role
        // This logic should be based on the user's roles
        // For now, we'll get projects from their current mission

        // Get employee's current mission
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
}