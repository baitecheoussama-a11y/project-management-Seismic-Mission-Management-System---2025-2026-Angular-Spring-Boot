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
import com.pfe.webapp.repository.EmployeRepository;
import com.pfe.webapp.repository.EquipeRepository;
import com.pfe.webapp.repository.team.AffectationEquipeRepository;
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
            // If no mission specified, return all equipes
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

        // Check if this team has members in the current mission
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
        return convertToDTO(saved);
    }

    // Update equipe
    public EquipeDTO updateEquipe(Long id, EquipeRequestDTO request) {
        Equipe equipe = equipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        equipe.setNom(request.getNom());
        equipe.setType(request.getType());

        Equipe updated = equipeRepository.save(equipe);
        return convertToDTO(updated);
    }

    // Delete equipe
    public void deleteEquipe(Long id) {
        Equipe equipe = equipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        // Check if equipe has members in any active mission
        long memberCount = affectationEmployeRepository.countActiveMembersByEquipe(id, LocalDate.now());
        if (memberCount > 0) {
            throw new RuntimeException("Cannot delete equipe with active members. Please reassign or remove members first.");
        }

        equipeRepository.delete(equipe);
    }

    // Get members of equipe (filtered by current mission)
    public List<EmployeDTO> getEquipeMembers(Long equipeId) {
        Long missionId = getCurrentMissionId();
        if (missionId == null || missionId == 0) {
            // If no mission, return all active members
            List<AffectationEmploye> affectations = affectationEmployeRepository
                    .findByEquipeIdAndDateFinAfterOrDateFinNull(equipeId, LocalDate.now());
            return affectations.stream()
                    .map(a -> convertEmployeToDTO(a.getEmploye()))
                    .collect(Collectors.toList());
        }
        return getEquipeMembersByIdForMission(equipeId, missionId);
    }

    // Assign employees to equipe (update their team)
    public void assignEmployeesToEquipe(Long equipeId, List<Long> employeIds) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe not found"));

        for (Long employeId : employeIds) {
            // Find active affectation for this employee
            List<AffectationEmploye> activeAffectations = affectationEmployeRepository
                    .findByEmployeIdAndDateFinAfterOrDateFinNull(employeId, LocalDate.now());

            for (AffectationEmploye affectation : activeAffectations) {
                affectation.setEquipe(equipe);
                affectationEmployeRepository.save(affectation);
            }
        }
    }

    // Remove employee from equipe
    public void removeEmployeeFromEquipe(Long equipeId, Long employeId) {
        List<AffectationEmploye> affectations = affectationEmployeRepository
                .findByEquipeIdAndEmployeIdAndDateFinAfterOrDateFinNull(equipeId, employeId, LocalDate.now());

        for (AffectationEmploye affectation : affectations) {
            affectation.setEquipe(null);
            affectationEmployeRepository.save(affectation);
        }
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
        dto.setPoste(""); // Empty string as requested
        return dto;
    }

    // Get equipe by employee ID (the team the employee is currently assigned to)
    public EquipeDTO getMyEquipe(Long employeId) {
        LocalDate currentDate = LocalDate.now();

        // Find active affectation for this employee
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

        // Get all employees in this mission
        List<AffectationEmploye> missionAffectations = affectationEmployeRepository
                .findByMissionIdAndDateFinAfterOrDateFinNull(missionId, currentDate);

        // Get employees already assigned to a team
        List<Long> assignedEmployeeIds = missionAffectations.stream()
                .filter(a -> a.getEquipe() != null)
                .map(a -> a.getEmploye().getId())
                .collect(Collectors.toList());

        // Filter out employees already in a team
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
    // في EquipeService.java - تعديل getAllEquipesWithActivities
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

            // ✅ استخدام Setters بدلاً من Constructor
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
        // Use findAllByEquipeIdAndMissionId (returns List, not Optional)
        List<AffectationEquipe> affectations = affectationEquipeRepository
                .findAllByEquipeIdAndMissionId(equipeId, missionId);

        return affectations.stream()
                .map(ae -> {
                    Active active = ae.getActive();
                    ActiveDTO dto = new ActiveDTO();
                    dto.setId(active.getId());
                    dto.setCodeActive(active.getCodeActive());
                    dto.setObjectif(active.getObjectif());
                    dto.setDescription(active.getDescription());
                    dto.setDateDebut(ae.getDateDebut());
                    dto.setDateFin(ae.getDateFin());
                    return dto;
                })
                .collect(Collectors.toList());
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

        // Use findAllByEquipeIdAndMissionId (returns List)
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

    // Helper method to convert Rapport to DTO
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

    // Helper method to convert Equipe to EquipeReportsDTO
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

    // Get all equipes with member count for a specific mission
    public List<EquipeDTO> getAllEquipesWithMemberCountForMission(Long missionId) {
        LocalDate currentDate = LocalDate.now();

        // Get all active affectations for this mission
        List<AffectationEmploye> missionAffectations = affectationEmployeRepository
                .findByMissionIdAndDateFinAfterOrDateFinNull(missionId, currentDate);

        // Group by equipe
        Map<Long, List<Employe>> membersByEquipe = new HashMap<>();

        for (AffectationEmploye aff : missionAffectations) {
            if (aff.getEquipe() != null) {
                Long equipeId = aff.getEquipe().getId();
                membersByEquipe.computeIfAbsent(equipeId, k -> new ArrayList<>())
                        .add(aff.getEmploye());
            }
        }

        // Build DTOs for only teams that have members in this mission
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

    // Get equipe members by equipe ID and mission ID
    public List<EmployeDTO> getEquipeMembersByIdForMission(Long equipeId, Long missionId) {
        LocalDate currentDate = LocalDate.now();

        List<AffectationEmploye> affectations = affectationEmployeRepository
                .findByEquipeIdAndMissionIdAndDateFinAfterOrDateFinNull(equipeId, missionId, currentDate);

        return affectations.stream()
                .map(a -> convertEmployeToDTO(a.getEmploye()))
                .collect(Collectors.toList());
    }

    // Helper method to count active members in mission
    private long getActiveMembersCountInMission(Long equipeId, Long missionId) {
        LocalDate currentDate = LocalDate.now();
        return affectationEmployeRepository
                .countByEquipeIdAndMissionIdAndDateFinAfterOrDateFinNull(equipeId, missionId, currentDate);
    }

    // Convert to DTO with mission context
    private EquipeDTO convertToDTOForMission(Equipe equipe, Long missionId) {
        EquipeDTO dto = new EquipeDTO();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setType(equipe.getType());

        long memberCount = getActiveMembersCountInMission(equipe.getId(), missionId);
        dto.setMemberCount((int) memberCount);

        return dto;
    }
}