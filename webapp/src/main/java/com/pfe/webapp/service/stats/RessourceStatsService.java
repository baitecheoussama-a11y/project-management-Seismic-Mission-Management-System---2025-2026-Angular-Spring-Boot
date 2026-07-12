package com.pfe.webapp.service.stats;

import com.pfe.webapp.dto.stats.*;
import com.pfe.webapp.entity.ressource.Ressource;
import com.pfe.webapp.repository.ressource.ConsommationRepository;
import com.pfe.webapp.repository.ressource.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RessourceStatsService {

    @Autowired
    private ConsommationRepository consommationRepository;

    @Autowired
    private RessourceRepository ressourceRepository;

    private static final double CRITICAL_STOCK_THRESHOLD = 10.0; // Less than 10 units is critical

    public RessourceStatsSummaryDTO getStatsSummary(LocalDate startDate, LocalDate endDate) {
        RessourceStatsSummaryDTO summary = new RessourceStatsSummaryDTO();

        // 1. Total Cost
        Double totalCost = consommationRepository.getTotalCost(startDate, endDate);
        summary.setTotalCost(totalCost != null ? totalCost : 0.0);

        // 2. Consommation by Ressource
        summary.setConsommationByRessource(
                consommationRepository.getConsommationByRessource(startDate, endDate)
        );

        // 3. Consommation by Mission
        summary.setConsommationByMission(
                consommationRepository.getConsommationByMission(startDate, endDate)
        );

        // 4. Cost by Ressource
        summary.setCostByRessource(
                consommationRepository.getCostByRessource(startDate, endDate)
        );

        // 5. Consommation by Month
        summary.setConsommationByMonth(
                consommationRepository.getConsommationByMonth(startDate, endDate)
        );

        // 6. Consommation by Type
        summary.setConsommationByType(
                consommationRepository.getConsommationByType(startDate, endDate)
        );

        // 7. Cost by Type
        summary.setCostByType(
                consommationRepository.getCostByType(startDate, endDate)
        );

        // 8. Top 5 Ressources
        summary.setTop5Ressources(
                consommationRepository.getTop5Ressources(startDate, endDate)
        );

        // 9. Critical Stock - ✅ FIXED: Calculates remaining stock from consumptions
        summary.setCriticalStock(getCriticalStock());

        return summary;
    }

    // ========== INDIVIDUAL STATS METHODS ==========

    public List<ConsommationStatsDTO> getConsommationByRessource(LocalDate startDate, LocalDate endDate) {
        return consommationRepository.getConsommationByRessource(startDate, endDate);
    }

    public List<ConsommationStatsDTO> getConsommationByMission(LocalDate startDate, LocalDate endDate) {
        return consommationRepository.getConsommationByMission(startDate, endDate);
    }

    public List<RessourceCostStatsDTO> getCostByRessource(LocalDate startDate, LocalDate endDate) {
        return consommationRepository.getCostByRessource(startDate, endDate);
    }

    public List<MonthlyConsommationDTO> getConsommationByMonth(LocalDate startDate, LocalDate endDate) {
        return consommationRepository.getConsommationByMonth(startDate, endDate);
    }

    public Double getTotalCost(LocalDate startDate, LocalDate endDate) {
        return consommationRepository.getTotalCost(startDate, endDate);
    }

    public List<ConsommationStatsDTO> getConsommationByType(LocalDate startDate, LocalDate endDate) {
        return consommationRepository.getConsommationByType(startDate, endDate);
    }

    public List<RessourceCostStatsDTO> getCostByType(LocalDate startDate, LocalDate endDate) {
        return consommationRepository.getCostByType(startDate, endDate);
    }

    public List<RessourceCostStatsDTO> getTop5Ressources(LocalDate startDate, LocalDate endDate) {
        return consommationRepository.getTop5Ressources(startDate, endDate);
    }

    // ✅ FIXED: Critical Stock - calculates remaining stock
    public List<RessourceCostStatsDTO> getCriticalStock() {
        List<Ressource> allResources = ressourceRepository.findAll();

        if (allResources.isEmpty()) {
            return new ArrayList<>();
        }

        List<RessourceCostStatsDTO> criticalResources = new ArrayList<>();

        for (Ressource ressource : allResources) {
            // Get total consumed for this resource
            Double totalConsumed = consommationRepository.sumValeurByRessourceId(ressource.getIdRessource());
            if (totalConsumed == null) {
                totalConsumed = 0.0;
            }

            // Calculate remaining stock
            double initialStock = ressource.getQuantite() != null ? ressource.getQuantite() : 0;
            double remainingStock = Math.max(0, initialStock - totalConsumed);

            // If remaining stock is below threshold, add to critical list
            if (remainingStock < CRITICAL_STOCK_THRESHOLD) {
                criticalResources.add(new RessourceCostStatsDTO(
                        ressource.getTitre(),
                        remainingStock,
                        ressource.getCout() != null ? ressource.getCout() * remainingStock : 0.0
                ));
            }
        }

        // Sort by remaining stock ascending (most critical first)
        criticalResources.sort(Comparator.comparing(RessourceCostStatsDTO::getValue));

        return criticalResources;
    }
}