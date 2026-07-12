package com.pfe.webapp.dto.stats;

public class ProductionTrendDTO {
    private String month;
    private Double productionValue;

    public ProductionTrendDTO() {}

    public ProductionTrendDTO(String month, Double productionValue) {
        this.month = month;
        this.productionValue = productionValue;
    }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public Double getProductionValue() { return productionValue; }
    public void setProductionValue(Double productionValue) { this.productionValue = productionValue; }
}