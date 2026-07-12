// src/main/java/com/pfe/webapp/dto/analytics/CostByMissionDTO.java
package com.pfe.webapp.dto.analytics;

public class CostByMissionDTO {
    private String missionCode;
    private String methodologie;
    private Integer projectCount;
    private Double totalCost;
    private Double totalBudget;
    private Double budgetUsagePercent;
    private Double avgProgression;

    // Default constructor
    public CostByMissionDTO() {
    }

    // All-args constructor
    public CostByMissionDTO(String missionCode, String methodologie, Integer projectCount,
                            Double totalCost, Double totalBudget, Double budgetUsagePercent,
                            Double avgProgression) {
        this.missionCode = missionCode;
        this.methodologie = methodologie;
        this.projectCount = projectCount;
        this.totalCost = totalCost;
        this.totalBudget = totalBudget;
        this.budgetUsagePercent = budgetUsagePercent;
        this.avgProgression = avgProgression;
    }

    // Getters and Setters
    public String getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(String missionCode) {
        this.missionCode = missionCode;
    }

    public String getMethodologie() {
        return methodologie;
    }

    public void setMethodologie(String methodologie) {
        this.methodologie = methodologie;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public Double getBudgetUsagePercent() {
        return budgetUsagePercent;
    }

    public void setBudgetUsagePercent(Double budgetUsagePercent) {
        this.budgetUsagePercent = budgetUsagePercent;
    }

    public Double getAvgProgression() {
        return avgProgression;
    }

    public void setAvgProgression(Double avgProgression) {
        this.avgProgression = avgProgression;
    }
}