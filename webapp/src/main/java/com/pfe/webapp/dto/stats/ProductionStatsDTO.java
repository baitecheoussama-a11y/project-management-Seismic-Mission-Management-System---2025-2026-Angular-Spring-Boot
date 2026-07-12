package com.pfe.webapp.dto.stats;

import java.util.List;

public class ProductionStatsDTO {
    // KPI Cards
    private Long totalProductionRecords;
    private Double averageProductivity;
    private Long activeTeamsCount;
    private Double averageActivityDuration;

    // Charts
    private List<ProductionByTeamDTO> productionByTeam;
    private List<ProductionByActivityDTO> productionByActivity;
    private List<ProductionTrendDTO> productionTrend;
    private List<ProductionByMissionDTO> productionByMission;

    // Tables
    private List<TopTeamDTO> top5Teams;
    private List<TopActivityDTO> top5Activities;

    // Advanced
    private List<ProductivityByTeamDTO> productivityByTeam;
    private List<ProductivityByActivityDTO> productivityByActivity;

    // Getters and Setters
    public Long getTotalProductionRecords() { return totalProductionRecords; }
    public void setTotalProductionRecords(Long totalProductionRecords) { this.totalProductionRecords = totalProductionRecords; }

    public Double getAverageProductivity() { return averageProductivity; }
    public void setAverageProductivity(Double averageProductivity) { this.averageProductivity = averageProductivity; }

    public Long getActiveTeamsCount() { return activeTeamsCount; }
    public void setActiveTeamsCount(Long activeTeamsCount) { this.activeTeamsCount = activeTeamsCount; }

    public Double getAverageActivityDuration() { return averageActivityDuration; }
    public void setAverageActivityDuration(Double averageActivityDuration) { this.averageActivityDuration = averageActivityDuration; }

    public List<ProductionByTeamDTO> getProductionByTeam() { return productionByTeam; }
    public void setProductionByTeam(List<ProductionByTeamDTO> productionByTeam) { this.productionByTeam = productionByTeam; }

    public List<ProductionByActivityDTO> getProductionByActivity() { return productionByActivity; }
    public void setProductionByActivity(List<ProductionByActivityDTO> productionByActivity) { this.productionByActivity = productionByActivity; }

    public List<ProductionTrendDTO> getProductionTrend() { return productionTrend; }
    public void setProductionTrend(List<ProductionTrendDTO> productionTrend) { this.productionTrend = productionTrend; }

    public List<ProductionByMissionDTO> getProductionByMission() { return productionByMission; }
    public void setProductionByMission(List<ProductionByMissionDTO> productionByMission) { this.productionByMission = productionByMission; }

    public List<TopTeamDTO> getTop5Teams() { return top5Teams; }
    public void setTop5Teams(List<TopTeamDTO> top5Teams) { this.top5Teams = top5Teams; }

    public List<TopActivityDTO> getTop5Activities() { return top5Activities; }
    public void setTop5Activities(List<TopActivityDTO> top5Activities) { this.top5Activities = top5Activities; }

    public List<ProductivityByTeamDTO> getProductivityByTeam() { return productivityByTeam; }
    public void setProductivityByTeam(List<ProductivityByTeamDTO> productivityByTeam) { this.productivityByTeam = productivityByTeam; }

    public List<ProductivityByActivityDTO> getProductivityByActivity() { return productivityByActivity; }
    public void setProductivityByActivity(List<ProductivityByActivityDTO> productivityByActivity) { this.productivityByActivity = productivityByActivity; }
}