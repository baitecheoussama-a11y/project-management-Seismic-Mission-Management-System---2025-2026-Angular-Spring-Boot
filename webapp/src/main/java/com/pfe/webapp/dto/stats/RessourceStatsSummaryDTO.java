package com.pfe.webapp.dto.stats;

import java.util.List;

public class RessourceStatsSummaryDTO {
    private Double totalCost;
    private List<ConsommationStatsDTO> consommationByRessource;
    private List<ConsommationStatsDTO> consommationByMission;
    private List<RessourceCostStatsDTO> costByRessource;
    private List<MonthlyConsommationDTO> consommationByMonth;
    private List<ConsommationStatsDTO> consommationByType;
    private List<RessourceCostStatsDTO> costByType;
    private List<RessourceCostStatsDTO> top5Ressources;
    private List<RessourceCostStatsDTO> criticalStock;

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public List<ConsommationStatsDTO> getConsommationByRessource() { return consommationByRessource; }
    public void setConsommationByRessource(List<ConsommationStatsDTO> consommationByRessource) {
        this.consommationByRessource = consommationByRessource;
    }

    public List<ConsommationStatsDTO> getConsommationByMission() { return consommationByMission; }
    public void setConsommationByMission(List<ConsommationStatsDTO> consommationByMission) {
        this.consommationByMission = consommationByMission;
    }

    public List<RessourceCostStatsDTO> getCostByRessource() { return costByRessource; }
    public void setCostByRessource(List<RessourceCostStatsDTO> costByRessource) {
        this.costByRessource = costByRessource;
    }

    public List<MonthlyConsommationDTO> getConsommationByMonth() { return consommationByMonth; }
    public void setConsommationByMonth(List<MonthlyConsommationDTO> consommationByMonth) {
        this.consommationByMonth = consommationByMonth;
    }

    public List<ConsommationStatsDTO> getConsommationByType() { return consommationByType; }
    public void setConsommationByType(List<ConsommationStatsDTO> consommationByType) {
        this.consommationByType = consommationByType;
    }

    public List<RessourceCostStatsDTO> getCostByType() { return costByType; }
    public void setCostByType(List<RessourceCostStatsDTO> costByType) {
        this.costByType = costByType;
    }

    public List<RessourceCostStatsDTO> getTop5Ressources() { return top5Ressources; }
    public void setTop5Ressources(List<RessourceCostStatsDTO> top5Ressources) {
        this.top5Ressources = top5Ressources;
    }

    public List<RessourceCostStatsDTO> getCriticalStock() { return criticalStock; }
    public void setCriticalStock(List<RessourceCostStatsDTO> criticalStock) {
        this.criticalStock = criticalStock;
    }
}