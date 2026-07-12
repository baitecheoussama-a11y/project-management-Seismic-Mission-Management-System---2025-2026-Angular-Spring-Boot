package com.pfe.webapp.service;

import com.pfe.webapp.dto.AffectationMaterielDTO;
import com.pfe.webapp.dto.BatchAffectationRequestDTO;
import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.entity.materiel.AffectationMateriel;
import com.pfe.webapp.entity.materiel.Materiel;
import com.pfe.webapp.repository.MissionRepository;
import com.pfe.webapp.repository.AffectationMaterielRepository;
import com.pfe.webapp.repository.MaterielRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AffectationMaterielService {

    @Autowired
    private AffectationMaterielRepository affectationRepository;

    @Autowired
    private MaterielRepository materielRepository;

    @Autowired
    private MissionRepository missionRepository;

    public List<AffectationMaterielDTO> getAllAffectations() {
        return affectationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AffectationMaterielDTO getAffectationById(Long id) {
        AffectationMateriel affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Affectation not found with id: " + id));
        return convertToDTO(affectation);
    }

    public List<AffectationMaterielDTO> getAffectationsByMateriel(Long materielId) {
        return affectationRepository.findByMaterielIdMateriel(materielId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AffectationMaterielDTO> getAffectationsByMission(Long missionId) {
        return affectationRepository.findByMissionId(missionId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AffectationMateriel> getByMissionId(Long missionId) {
        return affectationRepository.findByMissionId(missionId);
    }

    @Transactional
    public AffectationMaterielDTO createAffectation(AffectationMaterielDTO dto) {
        // 1. Validation des données
        validateAffectationRequest(dto);

        // 2. Récupération du matériel et de la mission
        Materiel materiel = materielRepository.findById(dto.getMaterielId())
                .orElseThrow(() -> new EntityNotFoundException("Materiel not found with id: " + dto.getMaterielId()));

        Mission mission = missionRepository.findById(dto.getMissionId())
                .orElseThrow(() -> new EntityNotFoundException("Mission not found with id: " + dto.getMissionId()));

        // 3. Création de l'affectation
        AffectationMateriel affectation = new AffectationMateriel();
        affectation.setDateDebut(dto.getDateDebut());
        affectation.setDateFin(dto.getDateFin());
        affectation.setMateriel(materiel);
        affectation.setMission(mission);

        AffectationMateriel saved = affectationRepository.save(affectation);

        return convertToDTO(saved);
    }

    @Transactional
    public List<AffectationMaterielDTO> createBatchAffectations(BatchAffectationRequestDTO batchRequest) {
        if (batchRequest.getMaterielIds() == null || batchRequest.getMaterielIds().isEmpty()) {
            throw new IllegalArgumentException("At least one materiel ID is required");
        }
        if (batchRequest.getMissionId() == null) {
            throw new IllegalArgumentException("Mission ID is required");
        }
        if (batchRequest.getDateDebut() == null) {
            throw new IllegalArgumentException("Start date is required");
        }

        List<AffectationMaterielDTO> result = new ArrayList<>();

        // Create assignments for all
        for (Long materielId : batchRequest.getMaterielIds()) {
            Materiel materiel = materielRepository.findById(materielId)
                    .orElseThrow(() -> new EntityNotFoundException("Materiel not found with id: " + materielId));
            Mission mission = missionRepository.findById(batchRequest.getMissionId())
                    .orElseThrow(() -> new EntityNotFoundException("Mission not found"));

            AffectationMateriel affectation = new AffectationMateriel();
            affectation.setDateDebut(batchRequest.getDateDebut());
            affectation.setDateFin(batchRequest.getDateFin());
            affectation.setMateriel(materiel);
            affectation.setMission(mission);

            AffectationMateriel saved = affectationRepository.save(affectation);
            result.add(convertToDTO(saved));
        }

        return result;
    }

    // Get available quantity - now just returns total count of affectations or 1 per materiel
    public Integer getAvailableQuantity(Long materielId) {
        Materiel materiel = materielRepository.findById(materielId).orElse(null);
        if (materiel == null) return 0;
        // For now, a materiel is available if it has no active affectations
        List<AffectationMateriel> activeAffectations = affectationRepository.findActiveByMaterielId(materielId);
        return activeAffectations.isEmpty() ? 1 : 0;
    }

    @Transactional
    public AffectationMaterielDTO updateAffectation(Long id, AffectationMaterielDTO dto) {
        AffectationMateriel affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Affectation not found with id: " + id));

        if (dto.getDateDebut() != null) {
            affectation.setDateDebut(dto.getDateDebut());
        }
        if (dto.getDateFin() != null) {
            affectation.setDateFin(dto.getDateFin());
        }

        if (dto.getMaterielId() != null && !dto.getMaterielId().equals(affectation.getMateriel().getIdMateriel())) {
            Materiel materiel = materielRepository.findById(dto.getMaterielId())
                    .orElseThrow(() -> new EntityNotFoundException("Materiel not found with id: " + dto.getMaterielId()));
            affectation.setMateriel(materiel);
        }

        if (dto.getMissionId() != null && !dto.getMissionId().equals(affectation.getMission().getId())) {
            Mission mission = missionRepository.findById(dto.getMissionId())
                    .orElseThrow(() -> new EntityNotFoundException("Mission not found with id: " + dto.getMissionId()));
            affectation.setMission(mission);
        }

        AffectationMateriel updated = affectationRepository.save(affectation);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteAffectation(Long id) {
        AffectationMateriel affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Affectation not found with id: " + id));
        affectationRepository.delete(affectation);
    }

    @Transactional
    public void deleteAffectationsByMateriel(Long materielId) {
        affectationRepository.deleteByMaterielIdMateriel(materielId);
    }

    @Transactional
    public void deleteAffectationsByMission(Long missionId) {
        affectationRepository.deleteByMissionId(missionId);
    }

    // ==================== HELPER METHODS ====================

    private void validateAffectationRequest(AffectationMaterielDTO dto) {
        if (dto.getMaterielId() == null) {
            throw new IllegalArgumentException("Materiel ID is required");
        }
        if (dto.getMissionId() == null) {
            throw new IllegalArgumentException("Mission ID is required");
        }
        if (dto.getDateDebut() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (dto.getDateFin() != null && dto.getDateFin().isBefore(dto.getDateDebut())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    private AffectationMaterielDTO convertToDTO(AffectationMateriel affectation) {
        AffectationMaterielDTO dto = new AffectationMaterielDTO();
        dto.setIdAffectation(affectation.getIdAffectation());
        dto.setDateDebut(affectation.getDateDebut());
        dto.setDateFin(affectation.getDateFin());

        if (affectation.getMateriel() != null) {
            dto.setMaterielId(affectation.getMateriel().getIdMateriel());
            dto.setMaterielCode(affectation.getMateriel().getCodeMateriel());
            dto.setMaterielMarque(affectation.getMateriel().getMarque());
            dto.setMaterielModele(affectation.getMateriel().getModele());
        }

        if (affectation.getMission() != null) {
            dto.setMissionId(affectation.getMission().getId());
            dto.setMissionCode(affectation.getMission().getCodeMission());
            dto.setMissionNom(affectation.getMission().getCodeMission());
        }

        return dto;
    }
}