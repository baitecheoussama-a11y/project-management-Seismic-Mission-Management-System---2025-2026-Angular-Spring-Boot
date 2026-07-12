package com.pfe.webapp.service;

import com.pfe.webapp.dto.AffectationRequestDTO;
import com.pfe.webapp.dto.EmployeDTO;
import com.pfe.webapp.dto.EquipeDTO;
import com.pfe.webapp.dto.MissionTeamDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.AffectationEmployeRepository;
import com.pfe.webapp.repository.CompteRepository;
import com.pfe.webapp.repository.EmployeRepository;
import com.pfe.webapp.repository.EquipeRepository;
import com.pfe.webapp.repository.MissionRepository;
import com.pfe.webapp.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MissionTeamService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private AffectationEmployeRepository affectationEmployeRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CompteRepository compteRepository;

    // Get all available employees (not assigned to any mission)
    public List<EmployeDTO> getAvailableEmployees() {
        List<Employe> allEmployees = employeRepository.findAll();
        List<AffectationEmploye> activeAffectations = affectationEmployeRepository.findActiveAffectations(LocalDate.now());

        Set<Long> assignedEmployeeIds = activeAffectations.stream()
                .map(a -> a.getEmploye().getId())
                .collect(Collectors.toSet());

        return allEmployees.stream()
                .map(emp -> convertToDTO(emp, !assignedEmployeeIds.contains(emp.getId()), null))
                .collect(Collectors.toList());
    }

    // Get all employees with their mission status
    public List<EmployeDTO> getAllEmployeesWithStatus() {
        List<Employe> allEmployees = employeRepository.findAll();
        List<AffectationEmploye> activeAffectations = affectationEmployeRepository.findActiveAffectations(LocalDate.now());

        Map<Long, Mission> employeeCurrentMission = new HashMap<>();
        for (AffectationEmploye aff : activeAffectations) {
            if (aff.getDateFin() == null || !aff.getDateFin().isBefore(LocalDate.now())) {
                employeeCurrentMission.put(aff.getEmploye().getId(), aff.getMission());
            }
        }

        return allEmployees.stream()
                .map(emp -> {
                    Mission currentMission = employeeCurrentMission.get(emp.getId());
                    return convertToDTO(emp, currentMission == null, currentMission);
                })
                .collect(Collectors.toList());
    }

    // Get mission team details
    public MissionTeamDTO getMissionTeam(Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        List<AffectationEmploye> affectations = affectationEmployeRepository.findByMissionIdAndDateFinAfterOrDateFinNull(
                missionId, LocalDate.now());

        List<EmployeDTO> members = new ArrayList<>();
        Map<Long, Equipe> employeeEquipe = new HashMap<>();

        for (AffectationEmploye aff : affectations) {
            Employe emp = aff.getEmploye();
            Equipe equipe = aff.getEquipe();
            members.add(convertToDTO(emp, true, mission));
            if (equipe != null) {
                employeeEquipe.put(emp.getId(), equipe);
            }
        }

        // Group by equipe
        Map<String, List<EmployeDTO>> membersByEquipe = new HashMap<>();
        for (EmployeDTO member : members) {
            Equipe equipe = employeeEquipe.get(member.getId());
            String equipeName = equipe != null ? equipe.getNom() : "Non assigné";
            membersByEquipe.computeIfAbsent(equipeName, k -> new ArrayList<>()).add(member);
        }

        // Get all equipes
        List<EquipeDTO> equipes = equipeRepository.findAll().stream()
                .map(this::convertToEquipeDTO)
                .collect(Collectors.toList());

        MissionTeamDTO dto = new MissionTeamDTO();
        dto.setMissionId(missionId);
        dto.setMissionName(mission.getCodeMission());
        dto.setTotalMembers(members.size());
        dto.setMembers(members);
        dto.setMembersByEquipe(membersByEquipe);
        dto.setEquipes(equipes);

        return dto;
    }

    // Add employees to mission
    public void addEmployeesToMission(AffectationRequestDTO request) {
        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        Equipe equipe = null;
        if (request.getEquipeId() != null) {
            equipe = equipeRepository.findById(request.getEquipeId())
                    .orElseThrow(() -> new RuntimeException("Equipe not found"));
        }

        LocalDate dateDebut = request.getDateDebut() != null ? request.getDateDebut() : LocalDate.now();
        LocalDate dateFin = request.getDateFin();

        List<String> employeeNames = new ArrayList<>();

        for (Long employeId : request.getEmployeIds()) {
            Employe employe = employeRepository.findById(employeId)
                    .orElseThrow(() -> new RuntimeException("Employe not found: " + employeId));

            // Check if employee is already assigned to an active mission
            List<AffectationEmploye> activeAffectations = affectationEmployeRepository
                    .findByEmployeIdAndDateFinAfterOrDateFinNull(employeId, LocalDate.now());

            if (!activeAffectations.isEmpty()) {
                throw new RuntimeException("Employee " + employe.getPrenom() + " " + employe.getNom() +
                        " is already assigned to a mission");
            }

            AffectationEmploye affectation = new AffectationEmploye();
            affectation.setEmploye(employe);
            affectation.setMission(mission);
            affectation.setEquipe(equipe);
            affectation.setDateDebut(dateDebut);
            affectation.setDateFin(dateFin);

            affectationEmployeRepository.save(affectation);
            employeeNames.add(employe.getPrenom() + " " + employe.getNom());
        }

        // ✅ Send notification
        sendEmployeesAddedToMissionNotification(mission, employeeNames, equipe);
    }

    // Update employee's team
    public void updateEmployeeTeam(Long missionId, Long employeId, Long equipeId) {
        AffectationEmploye affectation = affectationEmployeRepository
                .findByMissionIdAndEmployeIdAndDateFinAfterOrDateFinNull(missionId, employeId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Affectation not found"));

        Equipe newEquipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        Equipe oldEquipe = affectation.getEquipe();
        Employe employe = affectation.getEmploye();

        affectation.setEquipe(newEquipe);
        affectationEmployeRepository.save(affectation);

        // ✅ Send notification
        sendEmployeeTeamUpdatedNotification(employe, oldEquipe, newEquipe, missionId);
    }

    // Remove employee from mission
    public void removeEmployeeFromMission(Long missionId, Long employeId) {
        AffectationEmploye affectation = affectationEmployeRepository
                .findByMissionIdAndEmployeIdAndDateFinAfterOrDateFinNull(missionId, employeId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Affectation not found"));

        Employe employe = affectation.getEmploye();
        Equipe equipe = affectation.getEquipe();

        affectationEmployeRepository.delete(affectation);

        // ✅ Send notification
        sendEmployeeRemovedFromMissionNotification(employe, missionId, equipe);
    }

    // Get all equipes
    public List<EquipeDTO> getAllEquipes() {
        return equipeRepository.findAll().stream()
                .map(this::convertToEquipeDTO)
                .collect(Collectors.toList());
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

    private void sendEmployeesAddedToMissionNotification(Mission mission, List<String> employeeNames, Equipe equipe) {
        String title = "New Team Members Added";
        String message = String.format("%d employee(s) have been added to mission %s",
                employeeNames.size(),
                mission.getCodeMission());

        if (equipe != null) {
            message += " and assigned to team " + equipe.getNom();
        }

        String link = getMissionLink(mission.getId());  // ✅ FIXED

        List<Long> recipientIds = getRecipientCompteIdsForMission(mission.getId());

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

    private void sendEmployeeTeamUpdatedNotification(Employe employe, Equipe oldEquipe, Equipe newEquipe, Long missionId) {
        String title = "Team Assignment Updated";
        String message = String.format("%s %s has been moved from team '%s' to team '%s'",
                employe.getPrenom(),
                employe.getNom(),
                oldEquipe != null ? oldEquipe.getNom() : "No Team",
                newEquipe.getNom());

        String link = getMissionLink(missionId);  // ✅ FIXED

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

    private String getMissionLink(Long missionId) {
        return "/pages/mission/overview/" + missionId;
    }

    private void sendEmployeeRemovedFromMissionNotification(Employe employe, Long missionId, Equipe equipe) {
        String title = "Team Member Removed";
        String message = String.format("%s %s has been removed from mission",
                employe.getPrenom(),
                employe.getNom());

        if (equipe != null) {
            message += " (team: " + equipe.getNom() + ")";
        }

        String link = getMissionLink(missionId);  // ✅ FIXED

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

    // ========== CONVERTER METHODS ==========

    private EmployeDTO convertToDTO(Employe emp, boolean available, Mission currentMission) {
        EmployeDTO dto = new EmployeDTO();
        dto.setId(emp.getId());
        dto.setNom(emp.getNom());
        dto.setPrenom(emp.getPrenom());
        dto.setEmail(emp.getEmail());
        dto.setNumTel(emp.getNumTel());
        dto.setPoste("");
        dto.setAvailable(available);

        if (emp.getFonction() != null) {
            dto.setFonctionNom(emp.getFonction().getNom());
            dto.setFonctionId(emp.getFonction().getId());
        } else {
            dto.setFonctionNom(null);
            dto.setFonctionId(null);
        }

        if (currentMission != null) {
            dto.setCurrentMissionId(currentMission.getId());
            dto.setCurrentMissionName(currentMission.getCodeMission());
        }

        return dto;
    }

    private EquipeDTO convertToEquipeDTO(Equipe equipe) {
        EquipeDTO dto = new EquipeDTO();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setType(equipe.getType());

        long memberCount = affectationEmployeRepository.countActiveMembersByEquipe(equipe.getId(), LocalDate.now());
        dto.setMemberCount((int) memberCount);

        return dto;
    }
}