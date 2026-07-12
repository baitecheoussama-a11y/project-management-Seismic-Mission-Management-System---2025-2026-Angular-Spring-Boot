package com.pfe.webapp.dto.stats;

public class ProductionByMissionDTO {
    private String missionCode;
    private Double productionValue;

    public ProductionByMissionDTO() {}

    public ProductionByMissionDTO(String missionCode, Double productionValue) {
        this.missionCode = missionCode;
        this.productionValue = productionValue;
    }

    public String getMissionCode() { return missionCode; }
    public void setMissionCode(String missionCode) { this.missionCode = missionCode; }

    public Double getProductionValue() { return productionValue; }
    public void setProductionValue(Double productionValue) { this.productionValue = productionValue; }
}