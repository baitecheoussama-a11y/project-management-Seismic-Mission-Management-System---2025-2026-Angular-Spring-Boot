package com.pfe.webapp.service.materiel;

import com.pfe.webapp.dto.materiel.AffectationMaterielToActiveDTO;
import com.pfe.webapp.dto.materiel.AssignMaterielToActiveRequestDTO;
import com.pfe.webapp.dto.materiel.UpdateMaterielToActiveRequestDTO;
import com.pfe.webapp.entity.Active;
import com.pfe.webapp.entity.Project;
import com.pfe.webapp.entity.materiel.AffectationMaterielToActive;
import com.pfe.webapp.entity.materiel.Materiel;
import com.pfe.webapp.repository.materiel.AffectationMaterielToActiveRepository;
import com.pfe.webapp.repository.team.ActiveRepository;
import com.pfe.webapp.repository.ProjectRepository;
import com.pfe.webapp.repository.MaterielRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AffectationMaterielToActiveService {

    @Autowired
    private AffectationMaterielToActiveRepository affectationRepository;

    @Autowired
    private MaterielRepository materielRepository;

    @Autowired
    private ActiveRepository activeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    public AffectationMaterielToActiveDTO assignMaterielToActive(AssignMaterielToActiveRequestDTO request) {
        // Check if already assigned
        if (affectationRepository.existsByMaterielIdAndActiveId(request.getMaterielId(), request.getActiveId())) {
            throw new RuntimeException("Material is already assigned to this activity");
        }

        // Fetch entities
        Materiel materiel = materielRepository.findById(request.getMaterielId())
                .orElseThrow(() -> new RuntimeException("Materiel not found with id: " + request.getMaterielId()));

        Active active = activeRepository.findById(request.getActiveId())
                .orElseThrow(() -> new RuntimeException("Active not found with id: " + request.getActiveId()));

        // Create assignment
        AffectationMaterielToActive affectation = new AffectationMaterielToActive();
        affectation.setMateriel(materiel);
        affectation.setActive(active);
        affectation.setDateDebut(request.getDateDebut());
        affectation.setDateFin(request.getDateFin());

        AffectationMaterielToActive saved = affectationRepository.save(affectation);
        return convertToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<AffectationMaterielToActiveDTO> getByMaterielId(Long materielId) {
        return affectationRepository.findByMaterielIdWithDetails(materielId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AffectationMaterielToActiveDTO> getByActiveId(Long activeId) {
        return affectationRepository.findByActiveIdWithDetails(activeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AffectationMaterielToActiveDTO getById(Long id) {
        AffectationMaterielToActive affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation not found with id: " + id));
        return convertToDTO(affectation);
    }

    @Transactional
    public AffectationMaterielToActiveDTO update(Long id, UpdateMaterielToActiveRequestDTO request) {
        AffectationMaterielToActive affectation = affectationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation not found with id: " + id));

        if (request.getDateDebut() != null) {
            affectation.setDateDebut(request.getDateDebut());
        }
        if (request.getDateFin() != null) {
            affectation.setDateFin(request.getDateFin());
        }

        AffectationMaterielToActive updated = affectationRepository.save(affectation);
        return convertToDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!affectationRepository.existsById(id)) {
            throw new RuntimeException("Affectation not found with id: " + id);
        }
        affectationRepository.deleteById(id);
    }

    @Transactional
    public void deleteByMaterielAndActive(Long materielId, Long activeId) {
        affectationRepository.deleteByMaterielIdAndActiveId(materielId, activeId);
    }

    @Transactional(readOnly = true)
    public List<AffectationMaterielToActiveDTO> getActiveByMaterielId(Long materielId) {
        return affectationRepository.findActiveByMaterielId(materielId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AffectationMaterielToActiveDTO convertToDTO(AffectationMaterielToActive entity) {
        AffectationMaterielToActiveDTO dto = new AffectationMaterielToActiveDTO();
        dto.setIdAffectation(entity.getIdAffectation());
        dto.setDateDebut(entity.getDateDebut());
        dto.setDateFin(entity.getDateFin());
        dto.setIsActive(entity.isActiveAssignment());

        if (entity.getMateriel() != null) {
            dto.setMaterielId(entity.getMateriel().getIdMateriel());
            dto.setMaterielCode(entity.getMateriel().getCodeMateriel());
            dto.setMaterielDesignation(entity.getMateriel().getDesignation());
        }

        if (entity.getActive() != null) {
            dto.setActiveId(entity.getActive().getId());
            dto.setActiveCode(entity.getActive().getCodeActive());
            dto.setActiveObjectif(entity.getActive().getObjectif());
        }

        return dto;
    }
}