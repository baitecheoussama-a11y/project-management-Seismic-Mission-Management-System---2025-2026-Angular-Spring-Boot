package com.pfe.webapp.dto.stats;

public class MonthlyConsommationDTO {
    private String month;
    private Double value;

    public MonthlyConsommationDTO() {}

    public MonthlyConsommationDTO(String month, Double value) {
        this.month = month;
        this.value = value;
    }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
}