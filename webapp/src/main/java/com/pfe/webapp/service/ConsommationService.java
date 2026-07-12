package com.pfe.webapp.service;

import com.pfe.webapp.dto.ConsumptionDetailDTO;
import com.pfe.webapp.dto.ConsumptionRequestDTO;
import com.pfe.webapp.dto.MissionResourceDTO;
import com.pfe.webapp.dto.MissionResourceSummaryDTO;
import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.entity.ressource.*;
import com.pfe.webapp.repository.MissionRepository;
import com.pfe.webapp.repository.ressource.ConsommationRepository;
import com.pfe.webapp.repository.ressource.MotifRepository;
import com.pfe.webapp.repository.ressource.ContexteRepository;
import com.pfe.webapp.repository.ressource.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsommationService {

    @Autowired
    private ConsommationRepository consommationRepository;

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private MotifRepository motifRepository;

    @Autowired
    private ContexteRepository contexteRepository;

    @Autowired
    private MissionRepository missionRepository;

    // Get all consommations
    public List<Consommation> getAllConsommations() {
        return consommationRepository.findAll();
    }

    // Get consommation by id
    public Consommation getConsommationById(Long id) {
        return consommationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consommation not found with id: " + id));
    }

    // Get consommations by mission id
    public List<Consommation> getByMissionId(Long missionId) {
        return consommationRepository.findByMissionId(missionId);
    }

    // Get consommations by ressource id
    public List<Consommation> getByRessourceId(Long ressourceId) {
        return consommationRepository.findByRessource_IdRessource(ressourceId);
    }

    // Get consommations by date range
    public List<Consommation> getByDateBetween(LocalDate startDate, LocalDate endDate) {
        return consommationRepository.findByDateBetween(startDate, endDate);
    }

    // Get consommations by motif id
    public List<Consommation> getByMotifId(Long motifId) {
        return consommationRepository.findByMotif_IdMotif(motifId);
    }

    // Get consommations by contexte id
    public List<Consommation> getByContexteId(Long contexteId) {
        return consommationRepository.findByContexte_IdContexte(contexteId);
    }

    // Create new consommation with manual motif and contexte (NEW VERSION)
    public Consommation createConsommation(ConsumptionRequestDTO request) {
        Ressource resource = ressourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        Mission mission = missionRepository.findById(request.getMissionId())
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        // Create or find Motif by code
        Motif motif = motifRepository.findByCode(request.getMotifCode())
                .orElseGet(() -> {
                    Motif newMotif = new Motif();
                    newMotif.setCode(request.getMotifCode());
                    newMotif.setDescription(request.getMotifDescription() != null ? request.getMotifDescription() : "");
                    return motifRepository.save(newMotif);
                });

        // Create or find Contexte by title
        Contexte contexte = contexteRepository.findByTitre(request.getContexteTitle())
                .orElseGet(() -> {
                    Contexte newContexte = new Contexte();
                    newContexte.setTitre(request.getContexteTitle());
                    newContexte.setDescription(request.getContexteDescription() != null ? request.getContexteDescription() : "");
                    return contexteRepository.save(newContexte);
                });

        // Calculate total consumed across all missions for this resource
        Double totalConsumed = getTotalConsumedForResource(resource.getIdRessource());
        Double remainingStock = resource.getQuantite() - totalConsumed;

        if (remainingStock < request.getQuantity()) {
            throw new IllegalStateException("Insufficient stock. Available: " + remainingStock +
                    ", Requested: " + request.getQuantity());
        }

        Consommation consommation = new Consommation();
        consommation.setDate(request.getDate() != null ? request.getDate() : LocalDate.now());
        consommation.setValeur(request.getQuantity());
        consommation.setResume(request.getDescription());
        consommation.setRessource(resource);
        consommation.setMission(mission);
        consommation.setMotif(motif);
        consommation.setContexte(contexte);

        return consommationRepository.save(consommation);
    }

    // Get total consumed for a resource across all missions
    public Double getTotalConsumedForResource(Long resourceId) {
        Double sum = consommationRepository.sumValeurByRessourceId(resourceId);
        return sum != null ? sum : 0.0;
    }

    // Get mission resource summary with dynamic remaining calculation
    public MissionResourceSummaryDTO getMissionResourceSummary(Long missionId) {
        List<Consommation> missionConsommations = getByMissionId(missionId);
        List<Ressource> allResources = ressourceRepository.findAll();

        // Group mission consumptions by resource
        Map<Long, List<Consommation>> consumptionsByResource = missionConsommations.stream()
                .collect(Collectors.groupingBy(c -> c.getRessource().getIdRessource()));

        List<MissionResourceDTO> resources = new ArrayList<>();
        Double totalAllocated = 0.0;
        Double totalConsumedSum = 0.0;
        Double totalCost = 0.0;

        for (Ressource resource : allResources) {
            List<Consommation> consumptions = consumptionsByResource.getOrDefault(resource.getIdRessource(), new ArrayList<>());

            // Calculate total consumed across all missions for this resource
            Double totalConsumedAllMissions = getTotalConsumedForResource(resource.getIdRessource());

            // Calculate consumed by current mission
            Double consumedByMission = consumptions.stream().mapToDouble(Consommation::getValeur).sum();

            MissionResourceDTO dto = new MissionResourceDTO();
            dto.setResourceId(resource.getIdRessource());
            dto.setResourceName(resource.getTitre());
            dto.setTotalAllocated(resource.getQuantite());
            dto.setTotalConsumed(consumedByMission);
            dto.setRemaining(resource.getQuantite() - totalConsumedAllMissions);
            dto.setUnit(resource.getUnite());
            dto.setCostPerUnit(resource.getCout());

            List<ConsumptionDetailDTO> consumptionDetails = consumptions.stream()
                    .map(this::convertToDetailDTO)
                    .collect(Collectors.toList());
            dto.setConsumptions(consumptionDetails);

            resources.add(dto);

            totalAllocated += resource.getQuantite();
            totalConsumedSum += consumedByMission;
            totalCost += consumptions.stream().mapToDouble(c -> c.getValeur() * resource.getCout()).sum();
        }

        MissionResourceSummaryDTO summary = new MissionResourceSummaryDTO();
        summary.setTotalAllocated(totalAllocated);
        summary.setTotalConsumed(totalConsumedSum);
        summary.setTotalRemaining(totalAllocated - totalConsumedSum);
        summary.setTotalCost(totalCost);
        summary.setResources(resources);

        return summary;
    }

    private ConsumptionDetailDTO convertToDetailDTO(Consommation c) {
        ConsumptionDetailDTO dto = new ConsumptionDetailDTO();
        dto.setId(c.getIdConsommation());
        dto.setDate(c.getDate());
        dto.setQuantity(c.getValeur());
        dto.setDescription(c.getResume());

        if (c.getRessource() != null && c.getRessource().getCout() != null) {
            dto.setTotalCost(c.getValeur() * c.getRessource().getCout());
        } else {
            dto.setTotalCost(0.0);
        }

        if (c.getMotif() != null) {
            dto.setMotifCode(c.getMotif().getCode());
            dto.setMotifDescription(c.getMotif().getDescription());
        }

        if (c.getContexte() != null) {
            dto.setContexteTitle(c.getContexte().getTitre());
            dto.setContexteDescription(c.getContexte().getDescription());
        }

        return dto;
    }

    // Update consommation
    public Consommation updateConsommation(Long id, Consommation consommationDetails) {
        Consommation existingConsommation = getConsommationById(id);

        existingConsommation.setDate(consommationDetails.getDate());
        existingConsommation.setValeur(consommationDetails.getValeur());
        existingConsommation.setResume(consommationDetails.getResume());
        existingConsommation.setMotif(consommationDetails.getMotif());
        existingConsommation.setContexte(consommationDetails.getContexte());

        return consommationRepository.save(existingConsommation);
    }

    // Delete consommation
    public void deleteConsommation(Long id) {
        consommationRepository.deleteById(id);
    }

    // Get total consumed value by mission
    public Double getTotalConsumedByMission(Long missionId) {
        Double sum = consommationRepository.sumValeurByMissionId(missionId);
        return sum != null ? sum : 0.0;
    }

    // Get total cost by mission
    public Double getTotalCostByMission(Long missionId) {
        Double cost = consommationRepository.getTotalCostByMission(missionId);
        return cost != null ? cost : 0.0;
    }
}