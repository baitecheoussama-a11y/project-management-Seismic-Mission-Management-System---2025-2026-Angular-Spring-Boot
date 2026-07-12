package com.pfe.webapp.dto.stats;

public class RessourceCostStatsDTO {
    private String label;
    private Double value;
    private Double cost;

    public RessourceCostStatsDTO() {}

    public RessourceCostStatsDTO(String label, Double value, Double cost) {
        this.label = label;
        this.value = value;
        this.cost = cost;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
}