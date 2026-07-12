// src/main/java/com/pfe/webapp/dto/analytics/KPIDashboardDTO.java
package com.pfe.webapp.dto.analytics;

public class KPIDashboardDTO {
    private Double totalCost;
    private Integer totalProjects;
    private Double avgProgression;
    private Double avgBudgetUsage;
    private Integer activeProjects;
    private Integer completedProjects;
    private Integer delayedProjects;
    private Integer cancelledProjects;
    private Double costChangePercent;
    private Double progressionChangePercent;
    private Double avgCostPerProject;

    // Default constructor
    public KPIDashboardDTO() {
    }

    // All-args constructor
    public KPIDashboardDTO(Double totalCost, Integer totalProjects, Double avgProgression,
                           Double avgBudgetUsage, Integer activeProjects, Integer completedProjects,
                           Integer delayedProjects, Integer cancelledProjects, Double costChangePercent,
                           Double progressionChangePercent, Double avgCostPerProject) {
        this.totalCost = totalCost;
        this.totalProjects = totalProjects;
        this.avgProgression = avgProgression;
        this.avgBudgetUsage = avgBudgetUsage;
        this.activeProjects = activeProjects;
        this.completedProjects = completedProjects;
        this.delayedProjects = delayedProjects;
        this.cancelledProjects = cancelledProjects;
        this.costChangePercent = costChangePercent;
        this.progressionChangePercent = progressionChangePercent;
        this.avgCostPerProject = avgCostPerProject;
    }

    // Getters and Setters
    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Integer getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(Integer totalProjects) {
        this.totalProjects = totalProjects;
    }

    public Double getAvgProgression() {
        return avgProgression;
    }

    public void setAvgProgression(Double avgProgression) {
        this.avgProgression = avgProgression;
    }

    public Double getAvgBudgetUsage() {
        return avgBudgetUsage;
    }

    public void setAvgBudgetUsage(Double avgBudgetUsage) {
        this.avgBudgetUsage = avgBudgetUsage;
    }

    public Integer getActiveProjects() {
        return activeProjects;
    }

    public void setActiveProjects(Integer activeProjects) {
        this.activeProjects = activeProjects;
    }

    public Integer getCompletedProjects() {
        return completedProjects;
    }

    public void setCompletedProjects(Integer completedProjects) {
        this.completedProjects = completedProjects;
    }

    public Integer getDelayedProjects() {
        return delayedProjects;
    }

    public void setDelayedProjects(Integer delayedProjects) {
        this.delayedProjects = delayedProjects;
    }

    public Integer getCancelledProjects() {
        return cancelledProjects;
    }

    public void setCancelledProjects(Integer cancelledProjects) {
        this.cancelledProjects = cancelledProjects;
    }

    public Double getCostChangePercent() {
        return costChangePercent;
    }

    public void setCostChangePercent(Double costChangePercent) {
        this.costChangePercent = costChangePercent;
    }

    public Double getProgressionChangePercent() {
        return progressionChangePercent;
    }

    public void setProgressionChangePercent(Double progressionChangePercent) {
        this.progressionChangePercent = progressionChangePercent;
    }

    public Double getAvgCostPerProject() {
        return avgCostPerProject;
    }

    public void setAvgCostPerProject(Double avgCostPerProject) {
        this.avgCostPerProject = avgCostPerProject;
    }
}