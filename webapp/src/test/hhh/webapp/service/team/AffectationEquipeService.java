// service/AffectationEquipeService.java
package com.pfe.webapp.service.team;

import com.pfe.webapp.dto.team.AssignActivityRequestDTO;
import com.pfe.webapp.entity.AffectationEquipe;
import com.pfe.webapp.entity.Equipe;
import com.pfe.webapp.entity.Active;
import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.repository.team.AffectationEquipeRepository;
import com.pfe.webapp.repository.EquipeRepository;
import com.pfe.webapp.repository.team.ActiveRepository;
import com.pfe.webapp.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        // Create assignment
        AffectationEquipe affectationEquipe = new AffectationEquipe();
        affectationEquipe.setEquipe(equipe);
        affectationEquipe.setActive(active);
        affectationEquipe.setMission(mission);
        affectationEquipe.setDateDebut(request.getDateDebut());
        affectationEquipe.setDateFin(request.getDateFin());

        affectationEquipeRepository.save(affectationEquipe);
    }

    @Transactional
    public void removeActivityFromEquipe(Long equipeId, Long activeId, Long missionId) {
        if (!affectationEquipeRepository.existsByEquipeIdAndActiveIdAndMissionId(equipeId, activeId, missionId)) {
            throw new RuntimeException("Activity assignment not found");
        }

        affectationEquipeRepository.deleteByEquipeIdAndActiveIdAndMissionId(equipeId, activeId, missionId);
    }
}