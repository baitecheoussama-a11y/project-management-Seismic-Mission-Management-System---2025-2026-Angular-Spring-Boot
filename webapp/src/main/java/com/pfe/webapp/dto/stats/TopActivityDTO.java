package com.pfe.webapp.dto.stats;

public class TopActivityDTO {
    private Integer rank;
    private String activityCode;
    private String activityName;
    private Double productionValue;

    // ✅ Constructor for query (without rank)
    public TopActivityDTO(String activityCode, String activityName, Double productionValue) {
        this.activityCode = activityCode;
        this.activityName = activityName;
        this.productionValue = productionValue;
        this.rank = 0;
    }

    // ✅ Constructor with rank
    public TopActivityDTO(Integer rank, String activityCode, String activityName, Double productionValue) {
        this.rank = rank;
        this.activityCode = activityCode;
        this.activityName = activityName;
        this.productionValue = productionValue;
    }

    // Default constructor
    public TopActivityDTO() {}

    // Getters and Setters
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public String getActivityCode() { return activityCode; }
    public void setActivityCode(String activityCode) { this.activityCode = activityCode; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public Double getProductionValue() { return productionValue; }
    public void setProductionValue(Double productionValue) { this.productionValue = productionValue; }
}