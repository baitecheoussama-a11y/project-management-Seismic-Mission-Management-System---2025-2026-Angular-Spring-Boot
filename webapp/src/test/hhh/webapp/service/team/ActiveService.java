package com.pfe.webapp.service.team;

import com.pfe.webapp.dto.team.ActiveSimpleDTO;
import com.pfe.webapp.dto.team.ActiveRequestDTO;
import com.pfe.webapp.entity.Active;
import com.pfe.webapp.entity.AffectationEquipe;
import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.repository.team.ActiveRepository;
import com.pfe.webapp.repository.MissionRepository;
import com.pfe.webapp.repository.team.AffectationEquipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActiveService {

    @Autowired
    private ActiveRepository activeRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private AffectationEquipeRepository affectationEquipeRepository;

    @Transactional(readOnly = true)
    public List<ActiveSimpleDTO> getAllActives() {
        return activeRepository.findAllWithEquipes().stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActiveSimpleDTO> getActivesByMission(Long missionId) {
        List<Active> actives = affectationEquipeRepository.findActivesByMissionId(missionId);
        return actives.stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActiveSimpleDTO> getAvailableActives(Long missionId) {
        List<Active> availableActives = activeRepository.findActivesAvailableForMission(missionId);
        return availableActives.stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ActiveSimpleDTO getActiveById(Long id) {
        Active active = activeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Active not found with id: " + id));
        return convertToSimpleDTO(active);
    }

    @Transactional
    public ActiveSimpleDTO createActive(ActiveRequestDTO request) {
        // Check if Active with same code already exists (globally)
        if (activeRepository.existsByCodeActive(request.getCodeActive())) {
            throw new RuntimeException("Active with code '" + request.getCodeActive() + "' already exists");
        }

        // Create new Active
        Active active = new Active();
        active.setCodeActive(request.getCodeActive());
        active.setObjectif(request.getObjectif());
        active.setDescription(request.getDescription());

        Active saved = activeRepository.save(active);
        return convertToSimpleDTO(saved);
    }

    @Transactional
    public ActiveSimpleDTO updateActive(Long id, ActiveRequestDTO request) {
        Active active = activeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Active not found with id: " + id));

        // Get the mission this active belongs to (use the first affectation)
        List<AffectationEquipe> affectations = affectationEquipeRepository.findByActiveId(id);

        if (affectations.isEmpty()) {
            throw new RuntimeException("Active not assigned to any mission");
        }

        // Use the first affectation (all should have same mission)
        AffectationEquipe affectation = affectations.get(0);
        Long missionId = affectation.getMission().getId();

        // Check if updating to a new code that already exists in the same mission
        if (!active.getCodeActive().equals(request.getCodeActive())) {
            boolean existsInSameMission = affectationEquipeRepository.existsByActiveCodeAndMissionId(
                    request.getCodeActive(),
                    missionId
            );

            if (existsInSameMission) {
                throw new RuntimeException("Active with code '" + request.getCodeActive() +
                        "' already exists in this mission");
            }
        }

        active.setCodeActive(request.getCodeActive());
        active.setObjectif(request.getObjectif());
        active.setDescription(request.getDescription());

        // Update dates in AffectationEquipe if provided (update all affectations)
        if (request.getDateDebut() != null || request.getDateFin() != null) {
            for (AffectationEquipe ae : affectations) {
                if (request.getDateDebut() != null) {
                    ae.setDateDebut(request.getDateDebut());
                }
                if (request.getDateFin() != null) {
                    ae.setDateFin(request.getDateFin());
                }
                affectationEquipeRepository.save(ae);
            }
        }

        Active updated = activeRepository.save(active);
        return convertToSimpleDTO(updated);
    }

    @Transactional
    public void deleteActive(Long id) {
        Active active = activeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Active not found with id: " + id));

        // First delete all AffectationEquipe relationships
        List<AffectationEquipe> affectations = affectationEquipeRepository.findAssignmentsByActiveIdWithDetails(id);
        affectationEquipeRepository.deleteAll(affectations);

        // Then delete the active
        activeRepository.delete(active);
    }

    private ActiveSimpleDTO convertToSimpleDTO(Active active) {
        // Get mission info for this active if exists
        String missionCode = null;
        Long missionId = null;

        // Now returns List, not Optional
        List<AffectationEquipe> affectations = affectationEquipeRepository.findByActiveId(active.getId());

        if (!affectations.isEmpty() && affectations.get(0).getMission() != null) {
            missionCode = affectations.get(0).getMission().getCode();
            missionId = affectations.get(0).getMission().getId();
        }

        return new ActiveSimpleDTO(
                active.getId(),
                active.getCodeActive(),
                active.getObjectif(),
                active.getDescription(),
                active.getEquipes() != null ? active.getEquipes().size() : 0,
                missionCode,
                missionId
        );
    }
}