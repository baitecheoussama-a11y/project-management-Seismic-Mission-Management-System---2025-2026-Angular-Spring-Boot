package com.pfe.webapp.dto.stats;

public class ProductivityByTeamDTO {
    private String teamName;
    private Double productionValue;
    private Double hoursWorked;
    private Double productivity;

    public ProductivityByTeamDTO() {}

    public ProductivityByTeamDTO(String teamName, Double productionValue, Double hoursWorked) {
        this.teamName = teamName;
        this.productionValue = productionValue;
        this.hoursWorked = hoursWorked;
        this.productivity = hoursWorked > 0 ? productionValue / hoursWorked : 0;
    }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Double getProductionValue() { return productionValue; }
    public void setProductionValue(Double productionValue) { this.productionValue = productionValue; }

    public Double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }

    public Double getProductivity() { return productivity; }
    public void setProductivity(Double productivity) { this.productivity = productivity; }
}