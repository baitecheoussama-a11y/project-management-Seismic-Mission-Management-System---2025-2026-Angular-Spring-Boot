// service/MissionTeamService.java
package com.pfe.webapp.service;

import com.pfe.webapp.dto.AffectationRequestDTO;
import com.pfe.webapp.dto.EmployeDTO;
import com.pfe.webapp.dto.EquipeDTO;
import com.pfe.webapp.dto.MissionTeamDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.AffectationEmployeRepository;
import com.pfe.webapp.repository.EmployeRepository;
import com.pfe.webapp.repository.EquipeRepository;
import com.pfe.webapp.repository.MissionRepository;
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
        }
    }

    // Update employee's team
    public void updateEmployeeTeam(Long missionId, Long employeId, Long equipeId) {
        AffectationEmploye affectation = affectationEmployeRepository
                .findByMissionIdAndEmployeIdAndDateFinAfterOrDateFinNull(missionId, employeId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Affectation not found"));

        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        affectation.setEquipe(equipe);
        affectationEmployeRepository.save(affectation);
    }

    // Remove employee from mission
    public void removeEmployeeFromMission(Long missionId, Long employeId) {
        AffectationEmploye affectation = affectationEmployeRepository
                .findByMissionIdAndEmployeIdAndDateFinAfterOrDateFinNull(missionId, employeId, LocalDate.now())
                .orElseThrow(() -> new RuntimeException("Affectation not found"));

        affectationEmployeRepository.delete(affectation);
    }

    // Get all equipes
    public List<EquipeDTO> getAllEquipes() {
        return equipeRepository.findAll().stream()
                .map(this::convertToEquipeDTO)
                .collect(Collectors.toList());
    }

    // MissionTeamService.java - Update the convertToDTO method
    private EmployeDTO convertToDTO(Employe emp, boolean available, Mission currentMission) {
        EmployeDTO dto = new EmployeDTO();
        dto.setId(emp.getId());
        dto.setNom(emp.getNom());
        dto.setPrenom(emp.getPrenom());
        dto.setEmail(emp.getEmail());
        dto.setNumTel(emp.getNumTel());
        dto.setPoste(""); // Empty string instead of contract info
        dto.setAvailable(available);

        // ADD THIS: Set fonction information
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