package com.pfe.webapp.service;

import com.pfe.webapp.dto.*;
import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.entity.StatusMateriel;
import com.pfe.webapp.entity.materiel.*;
import com.pfe.webapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReparationService {

    @Autowired
    private ReparationRepository reparationRepository;

    @Autowired
    private ReparationInterneRepository reparationInterneRepository;

    @Autowired
    private ReparationExterneRepository reparationExterneRepository;

    @Autowired
    private MaterielRepository materielRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private AffectationMaterielRepository affectationRepository;

    // Declare broken equipment - from STOCK or MISSION
    public Reparation declarePanne(PanneRequestDTO request) {
        Materiel materiel = materielRepository.findById(request.getMaterielId())
                .orElseThrow(() -> new EntityNotFoundException("Materiel not found"));

        // Create base reparation record
        ReparationInterne reparation = new ReparationInterne();
        reparation.setDatePanne(request.getDatePanne());
        reparation.setDetailProbleme(request.getDetailProbleme());
        reparation.setMateriel(materiel);
        reparation.setStatus("PENDING");

        if (request.getMissionId() != null && request.getAffectationId() != null) {
            Mission mission = missionRepository.findById(request.getMissionId())
                    .orElseThrow(() -> new EntityNotFoundException("Mission not found"));
            reparation.setMission(mission);
            reparation.setSourceType("MISSION");
            reparation.setAffectationId(request.getAffectationId());
        } else {
            reparation.setSourceType("STOCK");
        }

        // Update materiel status
        materiel.setStatus(StatusMateriel.EN_PANNE);
        materielRepository.save(materiel);

        return reparationInterneRepository.save(reparation);
    }

    // Launch repair (choose INTERNE or EXTERNE)
    public Reparation launchRepair(LancementReparationDTO request) {
        Reparation baseReparation = reparationRepository.findById(request.getReparationId())
                .orElseThrow(() -> new EntityNotFoundException("Reparation not found"));

        Materiel materiel = baseReparation.getMateriel();

        // Save important info from old record
        String sourceType = baseReparation.getSourceType();
        Long affectationId = baseReparation.getAffectationId();
        Mission mission = baseReparation.getMission();

        if ("INTERNE".equals(request.getType())) {
            // Update materiel status
            materiel.setStatus(StatusMateriel.EN_REPARATION_INTERNE);
            materielRepository.save(materiel);

            // Create internal repair
            ReparationInterne interne = new ReparationInterne();
            interne.setDatePanne(baseReparation.getDatePanne());
            interne.setDetailProbleme(baseReparation.getDetailProbleme());
            interne.setMateriel(materiel);
            interne.setTechnicien(request.getTechnicien());
            interne.setStatus("IN_PROGRESS");

            // Copy important info from old record
            interne.setSourceType(sourceType);
            interne.setAffectationId(affectationId);
            interne.setMission(mission);

            // Delete old pending record
            reparationRepository.delete(baseReparation);

            return reparationInterneRepository.save(interne);

        } else if ("EXTERNE".equals(request.getType())) {
            // Update materiel status
            materiel.setStatus(StatusMateriel.EN_REPARATION_EXTERNE);
            materielRepository.save(materiel);

            // Create external repair
            ReparationExterne externe = new ReparationExterne();
            externe.setDatePanne(baseReparation.getDatePanne());
            externe.setDetailProbleme(baseReparation.getDetailProbleme());
            externe.setMateriel(materiel);
            externe.setFournisseur(request.getFournisseur());
            externe.setDateSortieChantier(request.getDateSortieChantier());
            externe.setStatus("SENT");

            // Copy important info from old record
            externe.setSourceType(sourceType);
            externe.setAffectationId(affectationId);
            externe.setMission(mission);

            // Delete old pending record
            reparationRepository.delete(baseReparation);

            return reparationExterneRepository.save(externe);
        }

        throw new IllegalArgumentException("Invalid repair type");
    }

    // Complete repair (mark as fixed)
    public Reparation completeRepair(FinReparationDTO request) {
        // Try to find in internal repairs first
        ReparationInterne interne = reparationInterneRepository.findById(request.getReparationId()).orElse(null);

        if (interne != null) {
            interne.setDateReparation(request.getDateReparation());
            interne.setCout(request.getCout());
            interne.setStatus("COMPLETED");

            Materiel materiel = interne.getMateriel();
            materiel.setStatus(StatusMateriel.EN_BON_ETAT);
            materielRepository.save(materiel);

            return reparationInterneRepository.save(interne);
        }

        // Try external repairs
        ReparationExterne externe = reparationExterneRepository.findById(request.getReparationId()).orElse(null);

        if (externe != null) {
            externe.setDateReparation(request.getDateReparation());
            externe.setCout(request.getCout());
            externe.setDateEntreeChantier(request.getDateEntreeChantier());
            externe.setStatus("COMPLETED");

            Materiel materiel = externe.getMateriel();
            materiel.setStatus(StatusMateriel.EN_BON_ETAT);
            materielRepository.save(materiel);

            return reparationExterneRepository.save(externe);
        }

        throw new EntityNotFoundException("Reparation not found");
    }

    // Get pending breakdowns (not yet launched for repair)
    public List<Reparation> getPendingReparations(Long materielId) {
        return reparationRepository.findPendingByMaterielId(materielId);
    }

    // Get ongoing internal repairs
    public List<ReparationInterne> getOngoingInternalRepairs(Long materielId) {
        return reparationInterneRepository.findOngoingByMaterielId(materielId);
    }

    // Get ongoing external repairs
    public List<ReparationExterne> getOngoingExternalRepairs(Long materielId) {
        return reparationExterneRepository.findSentByMaterielId(materielId);
    }

    // Get completed repairs
    public List<Reparation> getCompletedReparations(Long materielId) {
        return reparationRepository.findByMaterielIdAndDateReparationNotNull(materielId);
    }

    public List<Reparation> getAllReparationsByMateriel(Long materielId) {
        return reparationRepository.findByMaterielIdMateriel(materielId);
    }

    // Get single reparation by ID
    public Reparation getReparationById(Long id) {
        return reparationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reparation not found with id: " + id));
    }

    // Update pending breakdown (before launch)
    @Transactional
    public Reparation updatePanne(Long id, UpdatePanneRequestDTO request) {
        Reparation reparation = reparationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reparation not found with id: " + id));

        // Only allow update if status is PENDING
        if (!"PENDING".equals(reparation.getStatus())) {
            throw new IllegalStateException("Only pending breakdowns can be updated");
        }

        // Update fields
        if (request.getDatePanne() != null) {
            reparation.setDatePanne(request.getDatePanne());
        }
        if (request.getDetailProbleme() != null) {
            reparation.setDetailProbleme(request.getDetailProbleme());
        }

        return reparationRepository.save(reparation);
    }

    // Update internal repair (ongoing)
    @Transactional
    public ReparationInterne updateInternalRepair(Long id, UpdateInternalRepairRequestDTO request) {
        ReparationInterne reparation = reparationInterneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Internal repair not found with id: " + id));

        // Only allow update if status is IN_PROGRESS
        if (!"IN_PROGRESS".equals(reparation.getStatus())) {
            throw new IllegalStateException("Only ongoing internal repairs can be updated");
        }

        // Update fields
        if (request.getTechnicien() != null) {
            reparation.setTechnicien(request.getTechnicien());
        }
        if (request.getDetailProbleme() != null) {
            reparation.setDetailProbleme(request.getDetailProbleme());
        }

        return reparationInterneRepository.save(reparation);
    }

    // Update external repair (ongoing)
    @Transactional
    public ReparationExterne updateExternalRepair(Long id, UpdateExternalRepairRequestDTO request) {
        ReparationExterne reparation = reparationExterneRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("External repair not found with id: " + id));

        // Only allow update if status is SENT
        if (!"SENT".equals(reparation.getStatus())) {
            throw new IllegalStateException("Only ongoing external repairs can be updated");
        }

        // Update fields
        if (request.getFournisseur() != null) {
            reparation.setFournisseur(request.getFournisseur());
        }
        if (request.getDetailProbleme() != null) {
            reparation.setDetailProbleme(request.getDetailProbleme());
        }
        if (request.getDateSortieChantier() != null) {
            reparation.setDateSortieChantier(request.getDateSortieChantier());
        }

        return reparationExterneRepository.save(reparation);
    }

    // Delete reparation
    @Transactional
    public void deleteReparation(Long id) {
        Reparation reparation = reparationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reparation not found with id: " + id));

        Materiel materiel = reparation.getMateriel();

        // If it's a pending or ongoing repair, update the status
        if (reparation.getDateReparation() == null) {
            materiel.setStatus(StatusMateriel.EN_BON_ETAT);
            materielRepository.save(materiel);
        }

        reparationRepository.delete(reparation);
    }

    // Get repairs by materiel AND mission
    public List<Reparation> getReparationsByMaterielAndMission(Long materielId, Long missionId) {
        return reparationRepository.findByMaterielIdAndMissionId(materielId, missionId);
    }

    // Get pending repairs for a specific mission
    public List<Reparation> getPendingReparationsByMaterielAndMission(Long materielId, Long missionId) {
        return reparationRepository.findPendingByMaterielIdAndMissionId(materielId, missionId);
    }

    // Get ongoing repairs for a specific mission
    public List<Reparation> getOngoingReparationsByMaterielAndMission(Long materielId, Long missionId) {
        return reparationRepository.findOngoingByMaterielIdAndMissionId(materielId, missionId);
    }

    // Get completed repairs for a specific mission
    public List<Reparation> getCompletedReparationsByMaterielAndMission(Long materielId, Long missionId) {
        return reparationRepository.findCompletedByMaterielIdAndMissionId(materielId, missionId);
    }

    // Get all repairs (including completed) by materiel and mission
    public List<Reparation> getAllByMaterielAndMission(Long materielId, Long missionId) {
        return reparationRepository.findByMaterielIdAndMissionId(materielId, missionId);
    }

    // Get completed repairs by materiel and mission
    public List<Reparation> getCompletedByMaterielAndMission(Long materielId, Long missionId) {
        return reparationRepository.findCompletedByMaterielIdAndMissionId(materielId, missionId);
    }
}