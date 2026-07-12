package com.pfe.webapp.dto.stats;

public class ProductionByActivityDTO {
    private String activityCode;
    private String activityName;
    private Double productionValue;

    public ProductionByActivityDTO() {}

    public ProductionByActivityDTO(String activityCode, String activityName, Double productionValue) {
        this.activityCode = activityCode;
        this.activityName = activityName;
        this.productionValue = productionValue;
    }

    public String getActivityCode() { return activityCode; }
    public void setActivityCode(String activityCode) { this.activityCode = activityCode; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public Double getProductionValue() { return productionValue; }
    public void setProductionValue(Double productionValue) { this.productionValue = productionValue; }
}