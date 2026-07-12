package com.pfe.webapp.service;

import com.pfe.webapp.entity.AffectationEmploye;
import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.repository.AffectationEmployeRepository;
import com.pfe.webapp.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private AffectationEmployeRepository affectationEmployeRepository;

    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    public Mission getMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Mission non trouvée avec id: " + id));
    }

    public Mission createMission(Mission mission) {
        return missionRepository.save(mission);
    }

    public Mission updateMission(Long id, Mission missionDetails) {
        Mission mission = getMissionById(id);
        mission.setCodeMission(missionDetails.getCodeMission());
        mission.setMethodologie(missionDetails.getMethodologie());
        mission.setDescription(missionDetails.getDescription());
        return missionRepository.save(mission);
    }

    public void deleteMission(Long id) {
        Mission mission = getMissionById(id);
        missionRepository.delete(mission);
    }

    public List<Mission> getMissionsByMethodologie(String methodologie) {
        return missionRepository.findAll().stream()
                .filter(m -> m.getMethodologie().name().equals(methodologie))
                .toList();
    }

    public Map<String, Object> getMissionOverview(Long id) {
        Mission mission = getMissionById(id);

        Map<String, Object> overview = new HashMap<>();
        overview.put("id", mission.getId());
        overview.put("codeMission", mission.getCodeMission());
        overview.put("description", mission.getDescription());

        // Add only the data you need, avoiding circular references
        // Get first project if exists
        if (mission.getProjects() != null && !mission.getProjects().isEmpty()) {
            var project = mission.getProjects().get(0);
            Map<String, Object> projectMap = new HashMap<>();
            projectMap.put("id", project.getId());
            projectMap.put("name", project.getNom());
            projectMap.put("description", project.getDescription());
            projectMap.put("progress", project.getProgression());
            overview.put("currentProject", projectMap);
        } else {
            overview.put("currentProject", null);
        }

        return overview;
    }



    // Get current active mission for authenticated employee
    public Mission getCurrentMissionForEmployee(Long employeId) {
        LocalDate currentDate = LocalDate.now();

        Optional<AffectationEmploye> activeAffectation = affectationEmployeRepository
                .findActiveMissionByEmployeId(employeId, currentDate);

        return activeAffectation.map(AffectationEmploye::getMission).orElse(null);
    }

    // Check if employee has access to a specific mission
    public boolean hasAccessToMission(Long employeId, Long missionId) {
        LocalDate currentDate = LocalDate.now();

        return affectationEmployeRepository
                .findByMissionIdAndEmployeIdAndDateFinAfterOrDateFinNull(missionId, employeId, currentDate)
                .isPresent();
    }
    // Get full affectation details for employee
    public Optional<AffectationEmploye> getActiveAffectationForEmployee(Long employeId) {
        LocalDate currentDate = LocalDate.now();
        return affectationEmployeRepository.findActiveMissionByEmployeId(employeId, currentDate);
    }

}