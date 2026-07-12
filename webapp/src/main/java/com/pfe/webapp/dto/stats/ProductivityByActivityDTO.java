package com.pfe.webapp.dto.stats;

public class ProductivityByActivityDTO {
    private String activityCode;
    private String activityName;
    private Double productionValue;
    private Double hoursWorked;
    private Double productivity;

    public ProductivityByActivityDTO() {}

    public ProductivityByActivityDTO(String activityCode, String activityName, Double productionValue, Double hoursWorked) {
        this.activityCode = activityCode;
        this.activityName = activityName;
        this.productionValue = productionValue;
        this.hoursWorked = hoursWorked;
        this.productivity = hoursWorked > 0 ? productionValue / hoursWorked : 0;
    }

    public String getActivityCode() { return activityCode; }
    public void setActivityCode(String activityCode) { this.activityCode = activityCode; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public Double getProductionValue() { return productionValue; }
    public void setProductionValue(Double productionValue) { this.productionValue = productionValue; }

    public Double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }

    public Double getProductivity() { return productivity; }
    public void setProductivity(Double productivity) { this.productivity = productivity; }
}