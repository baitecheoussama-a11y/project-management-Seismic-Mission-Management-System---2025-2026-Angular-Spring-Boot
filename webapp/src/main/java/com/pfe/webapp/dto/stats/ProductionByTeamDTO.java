package com.pfe.webapp.dto.stats;

public class ProductionByTeamDTO {
    private String teamName;
    private Double productionValue;

    public ProductionByTeamDTO() {}

    public ProductionByTeamDTO(String teamName, Double productionValue) {
        this.teamName = teamName;
        this.productionValue = productionValue;
    }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Double getProductionValue() { return productionValue; }
    public void setProductionValue(Double productionValue) { this.productionValue = productionValue; }
}