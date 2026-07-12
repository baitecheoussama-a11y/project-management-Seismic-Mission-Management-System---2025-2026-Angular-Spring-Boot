package com.pfe.webapp.dto.stats;

public class ConsommationStatsDTO {
    private String label;
    private Double value;

    public ConsommationStatsDTO() {}

    public ConsommationStatsDTO(String label, Double value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
}