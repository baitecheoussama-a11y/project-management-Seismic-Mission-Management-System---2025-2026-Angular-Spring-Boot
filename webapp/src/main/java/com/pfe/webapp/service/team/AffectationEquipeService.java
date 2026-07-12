package com.pfe.webapp.service.team;

import com.pfe.webapp.dto.team.AffectationEquipeDTO;
import com.pfe.webapp.dto.team.AssignActivityRequestDTO;
import com.pfe.webapp.dto.team.UpdateAffectationEquipeRequestDTO;
import com.pfe.webapp.dto.team.UpdateRealDatesAndStatusRequestDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.*;
import com.pfe.webapp.repository.team.AffectationEquipeRepository;
import com.pfe.webapp.repository.team.ActiveRepository;
import com.pfe.webapp.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AffectationEquipeService {

    @Autowired
    private AffectationEquipeRepository affectationEquipeRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private ActiveRepository activeRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private AffectationEmployeRepository affectationEmployeRepository;

    @Transactional
    public void assignActivityToEquipe(AssignActivityRequestDTO request) {
        // Check if already assigned
        if (affectationEquipeRepository.existsByEquipeIdAndActiveIdAndMissionId(
                request.getEquipeId(), request.getActiveId(), request.getMissionId())) {
            throw new RuntimeException("Activity is already assigned to this team for this mission");
        }

        // Fetch entities
        Equipe equipe = equipeRepository.findById(request.getEquipeId())
                .orElseThrow(() -> new RuntimeException("Equipe not found with id: " + request.getEquipeId()));

        Active active = activeRepository.findById(request.getActiveId())
                .orElseThrow(() -> new RuntimeException("Active not found with id: " + request.getActiveId()));

        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + request.getMissionId()));

        Project project = null;
        if (request.getProjectId() != null) {
            project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found with id: " + request.getProjectId()));
        }

        // Create assignment
        AffectationEquipe affectationEquipe = new AffectationEquipe();
        affectationEquipe.setEquipe(equipe);
        affectationEquipe.setActive(active);
        affectationEquipe.setMission(mission);
        affectationEquipe.setProject(project);
        affectationEquipe.setDateDebut(request.getDateDebut());
        affectationEquipe.setDateFin(request.getDateFin());
        affectationEquipe.setDateStartReelle(request.getDateStartReelle());
        affectationEquipe.setDateFinReelle(request.getDateFinReelle());
        affectationEquipe.setOrdre(request.getOrdre() != null ? request.getOrdre() : 1);

        affectationEquipeRepository.save(affectationEquipe);

        // ✅ Send notification
        sendActivityAssignedNotification(affectationEquipe);
    }

    // Get all assignments for an equipe in a mission
    public List<AffectationEquipeDTO> getAssignmentsByEquipeAndMission(Long equipeId, Long missionId) {
        List<AffectationEquipe> assignments = affectationEquipeRepository.findAllByEquipeIdAndMissionIdWithDetails(equipeId, missionId);
        return assignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get a specific assignment by ID
    public AffectationEquipeDTO getAssignmentById(Long id) {
        AffectationEquipe assignment = affectationEquipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));
        return convertToDTO(assignment);
    }

    // Update real dates for an assignment
    @Transactional
    public AffectationEquipeDTO updateRealDates(Long id, UpdateAffectationEquipeRequestDTO request) {
        AffectationEquipe assignment = affectationEquipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));

        LocalDate oldStartReelle = assignment.getDateStartReelle();
        LocalDate oldFinReelle = assignment.getDateFinReelle();

        if (request.getDateStartReelle() != null) {
            assignment.setDateStartReelle(request.getDateStartReelle());
        }
        if (request.getDateFinReelle() != null) {
            assignment.setDateFinReelle(request.getDateFinReelle());
        }

        AffectationEquipe updated = affectationEquipeRepository.save(assignment);

        // ✅ Send notification for date update
        if ((request.getDateStartReelle() != null && !request.getDateStartReelle().equals(oldStartReelle)) ||
                (request.getDateFinReelle() != null && !request.getDateFinReelle().equals(oldFinReelle))) {
            sendActivityDatesUpdatedNotification(updated);
        }

        return convertToDTO(updated);
    }

    // Update real dates and auto-determine status
    @Transactional
    public AffectationEquipeDTO updateRealDatesAndStatus(Long activeId, Long missionId,
                                                         LocalDate dateStartReelle, LocalDate dateFinReelle) {
        // Find the assignment
        List<AffectationEquipe> assignments = affectationEquipeRepository.findByActiveIdAndMissionId(activeId, missionId);
        if (assignments.isEmpty()) {
            throw new RuntimeException("Assignment not found for active: " + activeId);
        }

        AffectationEquipe assignment = assignments.get(0);

        // Update real dates
        if (dateStartReelle != null) {
            assignment.setDateStartReelle(dateStartReelle);
        }
        if (dateFinReelle != null) {
            assignment.setDateFinReelle(dateFinReelle);
        }

        AffectationEquipe updated = affectationEquipeRepository.save(assignment);

        // ✅ Send notification for date update
        sendActivityDatesUpdatedNotification(updated);

        return convertToDTO(updated);
    }

    @Transactional
    public void removeActivityFromEquipe(Long equipeId, Long activeId, Long missionId) {
        AffectationEquipe assignment = affectationEquipeRepository
                .findByEquipeIdAndActiveIdAndMissionId(equipeId, activeId, missionId)
                .orElseThrow(() -> new RuntimeException("Activity assignment not found"));

        String activityCode = assignment.getActive() != null ? assignment.getActive().getCodeActive() : "Unknown";
        String equipeNom = assignment.getEquipe() != null ? assignment.getEquipe().getNom() : "Unknown";
        String projectNom = assignment.getProject() != null ? assignment.getProject().getNom() : "Unknown";

        affectationEquipeRepository.deleteByEquipeIdAndActiveIdAndMissionId(equipeId, activeId, missionId);

        // ✅ Send notification for removal
        sendActivityRemovedNotification(activityCode, equipeNom, projectNom, missionId);
    }

    // Convert to DTO
    private AffectationEquipeDTO convertToDTO(AffectationEquipe entity) {
        AffectationEquipeDTO dto = new AffectationEquipeDTO();
        dto.setId(entity.getId());
        dto.setDateDebut(entity.getDateDebut());
        dto.setDateFin(entity.getDateFin());
        dto.setDateStartReelle(entity.getDateStartReelle());
        dto.setDateFinReelle(entity.getDateFinReelle());
        dto.setOrdre(entity.getOrdre());

        if (entity.getEquipe() != null) {
            dto.setEquipeId(entity.getEquipe().getId());
            dto.setEquipeNom(entity.getEquipe().getNom());
        }

        if (entity.getActive() != null) {
            dto.setActiveId(entity.getActive().getId());
            dto.setActiveCode(entity.getActive().getCodeActive());
            dto.setActiveObjectif(entity.getActive().getObjectif());
        }

        if (entity.getProject() != null) {
            dto.setProjectId(entity.getProject().getId());
            dto.setProjectNom(entity.getProject().getNom());
        }

        if (entity.getMission() != null) {
            dto.setMissionId(entity.getMission().getId());
        }

        return dto;
    }

    // ========== NOTIFICATION HELPERS ==========

    private List<Long> getRecipientCompteIdsForMission(Long missionId) {
        List<Long> recipientIds = new ArrayList<>();

        // Get all ADMIN and DIRECTEUR comptes
        recipientIds.addAll(
                compteRepository.findComptesByRoleType(TypeRole.ADMINISTRATEUR)
                        .stream()
                        .map(Compte::getId)
                        .collect(Collectors.toList())
        );

        recipientIds.addAll(
                compteRepository.findComptesByRoleType(TypeRole.DIRECTEUR)
                        .stream()
                        .map(Compte::getId)
                        .collect(Collectors.toList())
        );

        // Get mission-specific users
        List<Long> missionSpecificIds = affectationEmployeRepository
                .findByMissionIdAndDateFinAfterOrDateFinNull(missionId, LocalDate.now())
                .stream()
                .map(AffectationEmploye::getEmploye)
                .filter(employe -> employe.getCompte() != null)
                .map(employe -> employe.getCompte().getId())
                .collect(Collectors.toList());

        recipientIds.addAll(missionSpecificIds);

        return recipientIds.stream().distinct().collect(Collectors.toList());
    }

    private void sendActivityAssignedNotification(AffectationEquipe assignment) {
        String title = "Activity Assigned";
        String message = String.format("Activity '%s' has been assigned to team '%s' for project '%s'",
                assignment.getActive().getCodeActive(),
                assignment.getEquipe().getNom(),
                assignment.getProject() != null ? assignment.getProject().getNom() : "No Project");

        String link = "/pages/activities";

        List<Long> recipientIds = getRecipientCompteIdsForMission(assignment.getMission().getId());

        if (!recipientIds.isEmpty()) {
            notificationService.createNotificationForUsers(
                    recipientIds,
                    title,
                    message,
                    NotificationType.ACTIVITY_ASSIGNED,
                    link
            );
        }
    }

    private void sendActivityDatesUpdatedNotification(AffectationEquipe assignment) {
        String title = "Activity Dates Updated";
        String message = String.format("Activity '%s' dates have been updated for team '%s'",
                assignment.getActive().getCodeActive(),
                assignment.getEquipe().getNom());

        if (assignment.getDateStartReelle() != null) {
            message += " (Start: " + assignment.getDateStartReelle() + ")";
        }
        if (assignment.getDateFinReelle() != null) {
            message += " (End: " + assignment.getDateFinReelle() + ")";
        }

        String link = "/pages/activities";

        List<Long> recipientIds = getRecipientCompteIdsForMission(assignment.getMission().getId());

        if (!recipientIds.isEmpty()) {
            notificationService.createNotificationForUsers(
                    recipientIds,
                    title,
                    message,
                    NotificationType.ACTIVITY_ASSIGNED,
                    link
            );
        }
    }

    private void sendActivityRemovedNotification(String activityCode, String equipeNom, String projectNom, Long missionId) {
        String title = "Activity Removed";
        String message = String.format("Activity '%s' has been removed from team '%s' for project '%s'",
                activityCode,
                equipeNom,
                projectNom);

        String link = "/pages/activities";

        List<Long> recipientIds = getRecipientCompteIdsForMission(missionId);

        if (!recipientIds.isEmpty()) {
            notificationService.createNotificationForUsers(
                    recipientIds,
                    title,
                    message,
                    NotificationType.ACTIVITY_ASSIGNED,
                    link
            );
        }
    }
}