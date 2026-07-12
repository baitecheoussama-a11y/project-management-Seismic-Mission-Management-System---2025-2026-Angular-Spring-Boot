package com.pfe.webapp.service;

import com.pfe.webapp.dto.EmployeDTO;
import com.pfe.webapp.dto.EquipeDTO;
import com.pfe.webapp.dto.EquipeRequestDTO;
import com.pfe.webapp.dto.rapport.RapportResponseDTO;
import com.pfe.webapp.dto.team.ActiveDTO;
import com.pfe.webapp.dto.team.EquipeActivitiesDTO;
import com.pfe.webapp.dto.team.EquipeReportsDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.AffectationEmployeRepository;
import com.pfe.webapp.repository.CompteRepository;
import com.pfe.webapp.repository.EmployeRepository;
import com.pfe.webapp.repository.EquipeRepository;
import com.pfe.webapp.repository.team.AffectationEquipeRepository;
import com.pfe.webapp.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import com.pfe.webapp.repository.ProjectRepository;

@Service
@Transactional
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AffectationEmployeRepository affectationEmployeRepository;

    @Autowired
    private AffectationEquipeRepository affectationEquipeRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CompteRepository compteRepository;

    // ThreadLocal to store current mission ID for request scope
    private static final ThreadLocal<Long> currentMissionIdHolder = new ThreadLocal<>();

    public static void setCurrentMissionId(Long missionId) {
        currentMissionIdHolder.set(missionId);
    }

    public static void clearCurrentMissionId() {
        currentMissionIdHolder.remove();
    }

    private Long getCurrentMissionId() {
        Long missionId = currentMissionIdHolder.get();
        return missionId != null ? missionId : 0L;
    }

    // Get all equipes (returns only teams with active members in current mission)
    public List<EquipeDTO> getAllEquipes() {
        Long missionId = getCurrentMissionId();
        if (missionId == null || missionId == 0) {
            return equipeRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        return getAllEquipesWithMemberCountForMission(missionId);
    }

    // Get equipe by id with members (only if team has members in current mission)
    public EquipeDTO getEquipeById(Long id) {
        Long missionId = getCurrentMissionId();
        Equipe equipe = equipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        if (missionId != null && missionId > 0) {
            long memberCount = getActiveMembersCountInMission(id, missionId);
            if (memberCount == 0) {
                throw new RuntimeException("Team not found for this mission");
            }
        }

        return convertToDTOWithMembers(equipe);
    }

    // Get equipes by type (only those with members in current mission)
    public List<EquipeDTO> getEquipesByType(TypeActivite type) {
        Long missionId = getCurrentMissionId();
        if (missionId == null || missionId == 0) {
            return equipeRepository.findByType(type).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        return equipeRepository.findByType(type).stream()
                .filter(equipe -> getActiveMembersCountInMission(equipe.getId(), missionId) > 0)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Create new equipe
    public EquipeDTO createEquipe(EquipeRequestDTO request) {
        Equipe equipe = new Equipe();
        equipe.setNom(request.getNom());
        equipe.setType(request.getType());

        Equipe saved = equipeRepository.save(equipe);

        sendTeamCreatedNotification(saved);

        return convertToDTO(saved);
    }

    // Update equipe
    public EquipeDTO updateEquipe(Long id, EquipeRequestDTO request) {
        Equipe equipe = equipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        String oldName = equipe.getNom();
        equipe.setNom(request.getNom());
        equipe.setType(request.getType());

        Equipe updated = equipeRepository.save(equipe);

        sendTeamUpdatedNotification(updated, oldName);

        return convertToDTO(updated);
    }

    // Delete equipe
    public void deleteEquipe(Long id) {
        Equipe equipe = equipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        String teamName = equipe.getNom();

        long memberCount = affectationEmployeRepository.countActiveMembersByEquipe(id, LocalDate.now());
        if (memberCount > 0) {
            throw new RuntimeException("Cannot delete equipe with active members. Please reassign or remove members first.");
        }

        equipeRepository.delete(equipe);

        sendTeamDeletedNotification(teamName);
    }

    // Get members of equipe (filtered by current mission)
    public List<EmployeDTO> getEquipeMembers(Long equipeId) {
        Long missionId = getCurrentMissionId();
        if (missionId == null || missionId == 0) {
            List<AffectationEmploye> affectations = affectationEmployeRepository
                    .findByEquipeIdAndDateFinAfterOrDateFinNull(equipeId, LocalDate.now());
            return affectations.stream()
                    .map(a -> convertEmployeToDTO(a.getEmploye()))
                    .collect(Collectors.toList());
        }
        return getEquipeMembersByIdForMission(equipeId, missionId);
    }

    // ✅ Assign employees to equipe (update their team) - WITH NOTIFICATIONS
    public void assignEmployeesToEquipe(Long equipeId, List<Long> employeIds) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        List<String> employeeNames = new ArrayList<>();

        for (Long employeId : employeIds) {
            Employe employe = employeRepository.findById(employeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found: " + employeId));

            List<AffectationEmploye> activeAffectations = affectationEmployeRepository
                    .findByEmployeIdAndDateFinAfterOrDateFinNull(employeId, LocalDate.now());

            for (AffectationEmploye affectation : activeAffectations) {
                affectation.setEquipe(equipe);
                affectationEmployeRepository.save(affectation);
                employeeNames.add(employe.getPrenom() + " " + employe.getNom());
            }
        }

        // ✅ Send notification
        sendEmployeesAssignedToTeamNotification(employeeNames, equipe);
    }

    // ✅ Remove employee from equipe - WITH NOTIFICATIONS
    public void removeEmployeeFromEquipe(Long equipeId, Long employeId) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeId));

        List<AffectationEmploye> affectations = affectationEmployeRepository
                .findByEquipeIdAndEmployeIdAndDateFinAfterOrDateFinNull(equipeId, employeId, LocalDate.now());

        for (AffectationEmploye affectation : affectations) {
            affectation.setEquipe(null);
            affectationEmployeRepository.save(affectation);
        }

        // ✅ Send notification
        sendEmployeeRemovedFromTeamNotification(employe, equipe);
    }

    // Helper methods
    private EquipeDTO convertToDTO(Equipe equipe) {
        Long missionId = getCurrentMissionId();
        EquipeDTO dto = new EquipeDTO();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setType(equipe.getType());

        long memberCount;
        if (missionId != null && missionId > 0) {
            memberCount = getActiveMembersCountInMission(equipe.getId(), missionId);
        } else {
            memberCount = affectationEmployeRepository.countActiveMembersByEquipe(equipe.getId(), LocalDate.now());
        }
        dto.setMemberCount((int) memberCount);

        return dto;
    }

    private EquipeDTO convertToDTOWithMembers(Equipe equipe) {
        EquipeDTO dto = convertToDTO(equipe);
        dto.setMembers(getEquipeMembers(equipe.getId()));
        return dto;
    }

    private EmployeDTO convertEmployeToDTO(Employe emp) {
        EmployeDTO dto = new EmployeDTO();
        dto.setId(emp.getId());
        dto.setNom(emp.getNom());
        dto.setPrenom(emp.getPrenom());
        dto.setEmail(emp.getEmail());
        dto.setNumTel(emp.getNumTel());
        dto.setPoste("");
        return dto;
    }

    // Get equipe by employee ID (the team the employee is currently assigned to)
    public EquipeDTO getMyEquipe(Long employeId) {
        LocalDate currentDate = LocalDate.now();

        List<AffectationEmploye> affectations = affectationEmployeRepository
                .findByEmployeIdAndDateFinAfterOrDateFinNull(employeId, currentDate);

        if (affectations.isEmpty()) {
            throw new RuntimeException("You are not assigned to any active mission");
        }

        Equipe equipe = affectations.get(0).getEquipe();
        if (equipe == null) {
            throw new RuntimeException("You are not assigned to any team");
        }

        return convertToDTOWithMembers(equipe);
    }

    // Get available employees for a specific mission (not yet in any team)
    public List<EmployeDTO> getAvailableEmployeesForMission(Long missionId) {
        LocalDate currentDate = LocalDate.now();

        List<AffectationEmploye> missionAffectations = affectationEmployeRepository
                .findByMissionIdAndDateFinAfterOrDateFinNull(missionId, currentDate);

        List<Long> assignedEmployeeIds = missionAffectations.stream()
                .filter(a -> a.getEquipe() != null)
                .map(a -> a.getEmploye().getId())
                .collect(Collectors.toList());

        return missionAffectations.stream()
                .filter(a -> !assignedEmployeeIds.contains(a.getEmploye().getId()))
                .map(a -> convertEmployeToDTO(a.getEmploye()))
                .collect(Collectors.toList());
    }

    // Get all equipes with member count (for sidebar) - FILTERED BY MISSION
    public List<EquipeDTO> getAllEquipesWithMemberCount() {
        Long missionId = getCurrentMissionId();
        if (missionId == null || missionId == 0) {
            return equipeRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }
        return getAllEquipesWithMemberCountForMission(missionId);
    }

    // Get equipe members by equipe ID - FILTERED BY MISSION
    public List<EmployeDTO> getEquipeMembersById(Long equipeId) {
        Long missionId = getCurrentMissionId();
        if (missionId == null || missionId == 0) {
            List<AffectationEmploye> affectations = affectationEmployeRepository
                    .findByEquipeIdAndDateFinAfterOrDateFinNull(equipeId, LocalDate.now());
            return affectations.stream()
                    .map(a -> convertEmployeToDTO(a.getEmploye()))
                    .collect(Collectors.toList());
        }
        return getEquipeMembersByIdForMission(equipeId, missionId);
    }

    // Get current user's equipe (for dashboard)
    public EquipeDTO getCurrentUserEquipe(Long employeId) {
        LocalDate currentDate = LocalDate.now();

        List<AffectationEmploye> affectations = affectationEmployeRepository
                .findByEmployeIdAndDateFinAfterOrDateFinNull(employeId, currentDate);

        if (affectations.isEmpty()) {
            return null;
        }

        Equipe equipe = affectations.get(0).getEquipe();
        if (equipe == null) {
            return null;
        }

        return convertToDTO(equipe);
    }

    // Get all equipes with activities - FILTERED BY MISSION
    public List<EquipeActivitiesDTO> getAllEquipesWithActivities() {
        List<Equipe> equipes = equipeRepository.findAll();
        List<EquipeActivitiesDTO> result = new ArrayList<>();
        Long missionId = getCurrentMissionId();

        for (Equipe equipe : equipes) {
            int activitiesCount = 0;

            if (missionId != null && missionId > 0) {
                activitiesCount = (int) affectationEquipeRepository.countByEquipeIdAndMissionId(equipe.getId(), missionId);
            } else {
                activitiesCount = (int) affectationEquipeRepository.countByEquipeId(equipe.getId());
            }

            long memberCount = affectationEmployeRepository.countActiveMembersByEquipe(equipe.getId(), LocalDate.now());

            EquipeActivitiesDTO dto = new EquipeActivitiesDTO();
            dto.setId(equipe.getId());
            dto.setNom(equipe.getNom());
            dto.setType(equipe.getType());
            dto.setMemberCount((int) memberCount);
            dto.setActivitiesCount(activitiesCount);

            result.add(dto);
        }
        return result;
    }

    // Get activities by equipe ID
    public List<ActiveDTO> getActivitiesByEquipeId(Long equipeId, Long missionId) {
        List<AffectationEquipe> assignments = affectationEquipeRepository
                .findAllByEquipeIdAndMissionIdWithDetails(equipeId, missionId);

        return assignments.stream()
                .map(this::convertToActiveDTO)
                .sorted(Comparator.comparing(ActiveDTO::getOrdre, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    private ActiveDTO convertToActiveDTO(AffectationEquipe assignment) {
        Active active = assignment.getActive();
        ActiveDTO dto = new ActiveDTO();

        dto.setId(active.getId());
        dto.setCodeActive(active.getCodeActive());
        dto.setObjectif(active.getObjectif());
        dto.setDescription(active.getDescription());

        dto.setDateDebut(assignment.getDateDebut());
        dto.setDateFin(assignment.getDateFin());
        dto.setDateStartReelle(assignment.getDateStartReelle());
        dto.setDateFinReelle(assignment.getDateFinReelle());
        dto.setOrdre(assignment.getOrdre());

        dto.setStatus(calculateStatus(assignment));
        dto.setProgression(calculateProgression(assignment));

        if (assignment.getProject() != null) {
            dto.setProjectId(assignment.getProject().getId());
            dto.setProjectNom(assignment.getProject().getNom());
        }

        return dto;
    }

    private String calculateStatus(AffectationEquipe assignment) {
        LocalDate dateDebut = assignment.getDateDebut();
        LocalDate dateFin = assignment.getDateFin();
        LocalDate dateStartReelle = assignment.getDateStartReelle();
        LocalDate dateFinReelle = assignment.getDateFinReelle();
        LocalDate now = LocalDate.now();

        if (dateDebut == null && dateFin == null && dateStartReelle == null && dateFinReelle == null) {
            return "ANNULE";
        }

        if (dateFinReelle != null) {
            return "TERMINI";
        }

        if (dateStartReelle != null) {
            if (dateStartReelle.isAfter(now)) {
                return "ENATTENTE";
            }
            if (dateFin != null && now.isAfter(dateFin)) {
                return "ENRETARD";
            }
            return "ENCOURS";
        }

        return "PLANIFIER";
    }

    private Double calculateProgression(AffectationEquipe assignment) {
        String status = calculateStatus(assignment);

        switch (status) {
            case "TERMINI": return 100.0;
            case "ENCOURS": return 50.0;
            case "ENRETARD": return 40.0;
            case "ENATTENTE": return 25.0;
            case "PLANIFIER": return 0.0;
            case "ANNULE": return 0.0;
            default: return 0.0;
        }
    }

    private EquipeActivitiesDTO convertToEquipeActivitiesDTO(Equipe equipe) {
        EquipeActivitiesDTO dto = new EquipeActivitiesDTO();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setType(equipe.getType());

        long memberCount = affectationEmployeRepository.countActiveMembersByEquipe(equipe.getId(), LocalDate.now());
        dto.setMemberCount((int) memberCount);

        List<AffectationEquipe> affectationEquipes = affectationEquipeRepository.findByEquipeId(equipe.getId());
        long activityCount = affectationEquipes.stream()
                .filter(ae -> ae.getActive() != null)
                .count();
        dto.setActivitiesCount((int) activityCount);

        return dto;
    }

    private EquipeActivitiesDTO convertToEquipeActivitiesDTOForMission(Equipe equipe, Long missionId) {
        EquipeActivitiesDTO dto = new EquipeActivitiesDTO();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setType(equipe.getType());

        long memberCount = getActiveMembersCountInMission(equipe.getId(), missionId);
        dto.setMemberCount((int) memberCount);

        List<AffectationEquipe> affectationEquipes = affectationEquipeRepository
                .findAllByEquipeIdAndMissionId(equipe.getId(), missionId);
        long activityCount = affectationEquipes.stream()
                .filter(ae -> ae.getActive() != null)
                .count();
        dto.setActivitiesCount((int) activityCount);

        return dto;
    }

    // Get all equipes with report counts - FILTERED BY MISSION
    public List<EquipeReportsDTO> getAllEquipesWithReportCounts() {
        Long missionId = getCurrentMissionId();
        if (missionId == null || missionId == 0) {
            return equipeRepository.findAll().stream()
                    .map(this::convertToEquipeReportsDTO)
                    .collect(Collectors.toList());
        }

        return getAllEquipesWithMemberCountForMission(missionId).stream()
                .map(dto -> {
                    Equipe equipe = equipeRepository.findById(dto.getId()).orElse(null);
                    return equipe != null ? convertToEquipeReportsDTOForMission(equipe, missionId) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Get reports by equipe ID
    public List<RapportResponseDTO> getReportsByEquipeId(Long equipeId, Long missionId) {
        Set<Rapport> allRapports = new HashSet<>();

        List<AffectationEquipe> affectationEquipes = affectationEquipeRepository
                .findAllByEquipeIdAndMissionId(equipeId, missionId);

        for (AffectationEquipe ae : affectationEquipes) {
            if (ae.getRapports() != null && !ae.getRapports().isEmpty()) {
                allRapports.addAll(ae.getRapports());
            }
        }

        Project currentProject = projectRepository.findCurrentActiveProjectByMissionId(missionId)
                .orElse(null);

        if (currentProject != null && currentProject.getRapports() != null) {
            allRapports.addAll(currentProject.getRapports());
        }

        return allRapports.stream()
                .map(this::convertRapportToDTO)
                .sorted((r1, r2) -> r2.getDate().compareTo(r1.getDate()))
                .collect(Collectors.toList());
    }

    private RapportResponseDTO convertRapportToDTO(Rapport rapport) {
        RapportResponseDTO dto = new RapportResponseDTO();
        dto.setId(rapport.getId());
        dto.setTitre(rapport.getTitre());
        dto.setDate(rapport.getDate());
        dto.setResume(rapport.getResume());

        if (rapport.getProject() != null) {
            dto.setProjectId(rapport.getProject().getId());
            dto.setProjectName(rapport.getProject().getNom());
            if (rapport.getProject().getMission() != null) {
                dto.setMissionCode(rapport.getProject().getMission().getCodeMission());
            }
        }

        return dto;
    }

    private EquipeReportsDTO convertToEquipeReportsDTO(Equipe equipe) {
        EquipeReportsDTO dto = new EquipeReportsDTO();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setType(equipe.getType());

        long memberCount = affectationEmployeRepository.countActiveMembersByEquipe(equipe.getId(), LocalDate.now());
        dto.setMemberCount((int) memberCount);

        List<AffectationEquipe> affectationEquipes = affectationEquipeRepository.findByEquipeId(equipe.getId());
        long reportCount = affectationEquipes.stream()
                .filter(ae -> ae.getRapports() != null)
                .mapToLong(ae -> ae.getRapports().size())
                .sum();
        dto.setReportCount((int) reportCount);

        return dto;
    }

    private EquipeReportsDTO convertToEquipeReportsDTOForMission(Equipe equipe, Long missionId) {
        EquipeReportsDTO dto = new EquipeReportsDTO();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setType(equipe.getType());

        long memberCount = getActiveMembersCountInMission(equipe.getId(), missionId);
        dto.setMemberCount((int) memberCount);

        List<AffectationEquipe> affectationEquipes = affectationEquipeRepository
                .findAllByEquipeIdAndMissionId(equipe.getId(), missionId);
        long reportCount = affectationEquipes.stream()
                .filter(ae -> ae.getRapports() != null)
                .mapToLong(ae -> ae.getRapports().size())
                .sum();
        dto.setReportCount((int) reportCount);

        return dto;
    }

    // NEW METHODS FOR MISSION FILTERING

    public List<EquipeDTO> getAllEquipesWithMemberCountForMission(Long missionId) {
        LocalDate currentDate = LocalDate.now();

        List<AffectationEmploye> missionAffectations = affectationEmployeRepository
                .findByMissionIdAndDateFinAfterOrDateFinNull(missionId, currentDate);

        Map<Long, List<Employe>> membersByEquipe = new HashMap<>();

        for (AffectationEmploye aff : missionAffectations) {
            if (aff.getEquipe() != null) {
                Long equipeId = aff.getEquipe().getId();
                membersByEquipe.computeIfAbsent(equipeId, k -> new ArrayList<>())
                        .add(aff.getEmploye());
            }
        }

        return membersByEquipe.keySet().stream()
                .map(equipeRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(equipe -> {
                    EquipeDTO dto = convertToDTOForMission(equipe, missionId);
                    List<Employe> members = membersByEquipe.get(equipe.getId());
                    dto.setMemberCount(members != null ? members.size() : 0);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<EmployeDTO> getEquipeMembersByIdForMission(Long equipeId, Long missionId) {
        LocalDate currentDate = LocalDate.now();

        List<AffectationEmploye> affectations = affectationEmployeRepository
                .findByEquipeIdAndMissionIdAndDateFinAfterOrDateFinNull(equipeId, missionId, currentDate);

        return affectations.stream()
                .map(a -> convertEmployeToDTO(a.getEmploye()))
                .collect(Collectors.toList());
    }

    private long getActiveMembersCountInMission(Long equipeId, Long missionId) {
        LocalDate currentDate = LocalDate.now();
        return affectationEmployeRepository
                .countByEquipeIdAndMissionIdAndDateFinAfterOrDateFinNull(equipeId, missionId, currentDate);
    }

    private EquipeDTO convertToDTOForMission(Equipe equipe, Long missionId) {
        EquipeDTO dto = new EquipeDTO();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setType(equipe.getType());

        long memberCount = getActiveMembersCountInMission(equipe.getId(), missionId);
        dto.setMemberCount((int) memberCount);

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
        if (missionId != null && missionId > 0) {
            List<Long> missionSpecificIds = affectationEmployeRepository
                    .findByMissionIdAndDateFinAfterOrDateFinNull(missionId, LocalDate.now())
                    .stream()
                    .map(AffectationEmploye::getEmploye)
                    .filter(employe -> employe.getCompte() != null)
                    .map(employe -> employe.getCompte().getId())
                    .collect(Collectors.toList());

            recipientIds.addAll(missionSpecificIds);
        }

        return recipientIds.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Get the appropriate link based on user role
     * - ADMIN and DIRECTEUR: /pages/mission/overview/{missionId}
     * - Others: /pages/teams/members
     */
    private String getNotificationLink(Long missionId) {
        // Check if the current user has ADMIN or DIRECTEUR role
        // We need to get the current user from the security context
        // For now, we'll check if missionId is provided, if yes use mission link
        // This logic should be enhanced with proper role checking
        if (missionId != null && missionId > 0) {
            // Check if user has ADMIN or DIRECTEUR role
            // This is a simplified version - you should inject the actual user
            // and check roles properly
            return "/pages/mission/overview/" + missionId;
        }
        return "/pages/teams/members";
    }

    private String getMissionLink(Long missionId) {
        return "/pages/mission/overview/" + missionId;
    }

    private String getTeamsMembersLink() {
        return "/pages/teams/members";
    }

    // ========== TEAM NOTIFICATIONS ==========

    private void sendTeamCreatedNotification(Equipe equipe) {
        String title = "New Team Created";
        String message = String.format("Team '%s' (Type: %s) has been created",
                equipe.getNom(),
                equipe.getType() != null ? equipe.getType().name() : "Unknown");

        Long missionId = getCurrentMissionId();
        String link = missionId != null && missionId > 0 ? getMissionLink(missionId) : getTeamsMembersLink();

        List<Long> recipientIds = getRecipientCompteIdsForMission(missionId);

        if (!recipientIds.isEmpty()) {
            notificationService.createNotificationForUsers(
                    recipientIds,
                    title,
                    message,
                    NotificationType.TEAM_ASSIGNED,
                    link
            );
        }
    }

    private void sendTeamUpdatedNotification(Equipe equipe, String oldName) {
        String title = "Team Updated";
        String message = String.format("Team '%s' (was '%s') has been updated",
                equipe.getNom(),
                oldName);

        Long missionId = getCurrentMissionId();
        String link = missionId != null && missionId > 0 ? getMissionLink(missionId) : getTeamsMembersLink();

        List<Long> recipientIds = getRecipientCompteIdsForMission(missionId);

        if (!recipientIds.isEmpty()) {
            notificationService.createNotificationForUsers(
                    recipientIds,
                    title,
                    message,
                    NotificationType.TEAM_ASSIGNED,
                    link
            );
        }
    }

    private void sendTeamDeletedNotification(String teamName) {
        String title = "Team Deleted";
        String message = String.format("Team '%s' has been deleted", teamName);

        Long missionId = getCurrentMissionId();
        String link = missionId != null && missionId > 0 ? getMissionLink(missionId) : getTeamsMembersLink();

        List<Long> recipientIds = getRecipientCompteIdsForMission(missionId);

        if (!recipientIds.isEmpty()) {
            notificationService.createNotificationForUsers(
                    recipientIds,
                    title,
                    message,
                    NotificationType.TEAM_ASSIGNED,
                    link
            );
        }
    }

    // ========== EMPLOYEE TEAM NOTIFICATIONS ==========

    private void sendEmployeesAssignedToTeamNotification(List<String> employeeNames, Equipe equipe) {
        String title = "Employees Assigned to Team";
        String message = String.format("%d employee(s) have been assigned to team '%s': %s",
                employeeNames.size(),
                equipe.getNom(),
                String.join(", ", employeeNames));

        Long missionId = getCurrentMissionId();
        String link = missionId != null && missionId > 0 ? getMissionLink(missionId) : getTeamsMembersLink();

        List<Long> recipientIds = getRecipientCompteIdsForMission(missionId);

        if (!recipientIds.isEmpty()) {
            notificationService.createNotificationForUsers(
                    recipientIds,
                    title,
                    message,
                    NotificationType.TEAM_ASSIGNED,
                    link
            );
        }
    }

    private void sendEmployeeRemovedFromTeamNotification(Employe employe, Equipe equipe) {
        String title = "Employee Removed from Team";
        String message = String.format("%s %s has been removed from team '%s'",
                employe.getPrenom(),
                employe.getNom(),
                equipe.getNom());

        Long missionId = getCurrentMissionId();
        String link = missionId != null && missionId > 0 ? getMissionLink(missionId) : getTeamsMembersLink();

        List<Long> recipientIds = getRecipientCompteIdsForMission(missionId);

        if (!recipientIds.isEmpty()) {
            notificationService.createNotificationForUsers(
                    recipientIds,
                    title,
                    message,
                    NotificationType.TEAM_ASSIGNED,
                    link
            );
        }
    }
}