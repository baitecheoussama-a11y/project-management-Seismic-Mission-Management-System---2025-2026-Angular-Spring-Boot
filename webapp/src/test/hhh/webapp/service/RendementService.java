package com.pfe.webapp.service;

import com.pfe.webapp.dto.rendement.RendementRequestDTO;
import com.pfe.webapp.dto.rendement.RendementResponseDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.team.ActiveRepository;  // You need to create this
import com.pfe.webapp.repository.EquipeRepository;
import com.pfe.webapp.repository.MissionRepository;
import com.pfe.webapp.repository.rapport.RapportRepository;
import com.pfe.webapp.repository.rapport.RendementRepository;
import com.pfe.webapp.repository.team.AffectationEquipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RendementService {

    private static final Logger log = LoggerFactory.getLogger(RendementService.class);

    @Autowired
    private RendementRepository rendementRepository;

    @Autowired
    private RapportRepository rapportRepository;

    @Autowired
    private AffectationEquipeRepository affectationEquipeRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ActiveRepository activeRepository;  // ✅ ADD THIS

    @Transactional(readOnly = true)
    public List<RendementResponseDTO> getRendementsByRapport(Long rapportId) {
        return rendementRepository.findByRapportId(rapportId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RendementResponseDTO addRendementToRapport(Long rapportId, Long equipeId, RendementRequestDTO request) {
        log.info("=== Adding Rendement ===");
        log.info("rapportId: {}, equipeId: {}", rapportId, equipeId);
        log.info("activeId from request: {}", request.getActiveId());

        // 1. Get the rapport
        Rapport rapport = rapportRepository.findById(rapportId)
                .orElseThrow(() -> new RuntimeException("Rapport not found with id: " + rapportId));

        log.info("Rapport found: {}", rapport.getTitre());

        // 2. Get the mission ID from the rapport's project
        Long missionId = rapport.getProject().getMission().getId();
        log.info("Mission ID from rapport: {}", missionId);

        // 3. Validate activeId is provided
        Long activeId = request.getActiveId();
        if (activeId == null) {
            throw new RuntimeException("activeId is required to create a rendement");
        }

        // 4. Get the Active entity
        Active active = activeRepository.findById(activeId)
                .orElseThrow(() -> new RuntimeException("Active not found with id: " + activeId));
        log.info("Active found: {}", active.getCodeActive());

        // 5. Find AffectationEquipe by equipeId, missionId, AND activeId
        List<AffectationEquipe> affectationList = affectationEquipeRepository
                .findByEquipeIdAndMissionIdAndActiveId(equipeId, missionId, activeId);

        AffectationEquipe affectationEquipe;

        if (affectationList.isEmpty()) {
            // Create new AffectationEquipe
            log.info("No AffectationEquipe found, creating new one for equipe: {}, mission: {}, active: {}",
                    equipeId, missionId, activeId);

            Equipe equipe = equipeRepository.findById(equipeId)
                    .orElseThrow(() -> new RuntimeException("Equipe not found with id: " + equipeId));

            Mission mission = missionRepository.findById(missionId)
                    .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

            AffectationEquipe newAe = new AffectationEquipe();
            newAe.setEquipe(equipe);
            newAe.setMission(mission);
            newAe.setActive(active);  // ✅ Link the active
            newAe.setDateDebut(LocalDate.now());

            affectationEquipe = affectationEquipeRepository.save(newAe);
            log.info("Created new AffectationEquipe with id: {}", affectationEquipe.getId());
        } else {
            affectationEquipe = affectationList.get(0);
            log.info("Found existing AffectationEquipe with id: {}, activeId: {}",
                    affectationEquipe.getId(),
                    affectationEquipe.getActive() != null ? affectationEquipe.getActive().getId() : null);
        }

        // 6. Create and save the rendement
        Rendement rendement = new Rendement();
        rendement.setHeureDebut(request.getHeureDebut());
        rendement.setHeureFin(request.getHeureFin());
        rendement.setValeurRendement(request.getValeurRendement());
        rendement.setUniteRendement(request.getUniteRendement());
        rendement.setDate(request.getDate());
        rendement.setRapport(rapport);
        rendement.setAffectationEquipe(affectationEquipe);

        double duration = calculateDuration(request.getHeureDebut(), request.getHeureFin());
        rendement.setDureeHeures(duration);

        Rendement saved = rendementRepository.save(rendement);
        log.info("Rendement saved with id: {}, affectationEquipeId: {}", saved.getId(), saved.getAffectationEquipe().getId());

        return convertToDTO(saved);
    }

    @Transactional
    public RendementResponseDTO updateRendement(Long id, RendementRequestDTO request) {
        Rendement rendement = rendementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendement not found with id: " + id));

        rendement.setHeureDebut(request.getHeureDebut());
        rendement.setHeureFin(request.getHeureFin());
        rendement.setValeurRendement(request.getValeurRendement());
        rendement.setUniteRendement(request.getUniteRendement());
        rendement.setDate(request.getDate());

        double duration = calculateDuration(request.getHeureDebut(), request.getHeureFin());
        rendement.setDureeHeures(duration);

        Rendement updated = rendementRepository.save(rendement);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteRendement(Long id) {
        rendementRepository.deleteById(id);
    }

    private double calculateDuration(java.time.LocalTime start, java.time.LocalTime end) {
        Duration duration = Duration.between(start, end);
        return duration.toMinutes() / 60.0;
    }

    private RendementResponseDTO convertToDTO(Rendement rendement) {
        double dureeHeures = 0;
        if (rendement.getHeureDebut() != null && rendement.getHeureFin() != null) {
            dureeHeures = calculateDuration(rendement.getHeureDebut(), rendement.getHeureFin());
        }

        Long activeId = null;
        if (rendement.getAffectationEquipe() != null && rendement.getAffectationEquipe().getActive() != null) {
            activeId = rendement.getAffectationEquipe().getActive().getId();
        }

        return new RendementResponseDTO(
                rendement.getId(),
                rendement.getHeureDebut(),
                rendement.getHeureFin(),
                rendement.getValeurRendement(),
                rendement.getUniteRendement(),
                rendement.getDate(),
                dureeHeures,
                rendement.getRapport() != null ? rendement.getRapport().getId() : null,
                rendement.getAffectationEquipe() != null ? rendement.getAffectationEquipe().getId() : null,
                activeId  // ✅ Add activeId to DTO
        );
    }

    @Transactional(readOnly = true)
    public List<RendementResponseDTO> getRendementsByRapportAndEquipe(Long rapportId, Long equipeId) {
        return rendementRepository.findByRapportIdAndEquipeId(rapportId, equipeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RendementResponseDTO> getRendementsByEquipeAndMission(Long equipeId, Long missionId) {
        return rendementRepository.findByEquipeIdAndMissionId(equipeId, missionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}