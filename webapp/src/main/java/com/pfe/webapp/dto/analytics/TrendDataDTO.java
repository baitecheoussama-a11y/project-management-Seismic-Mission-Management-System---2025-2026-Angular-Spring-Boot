// src/main/java/com/pfe/webapp/dto/analytics/TrendDataDTO.java
package com.pfe.webapp.dto.analytics;

import java.time.LocalDate;

public class TrendDataDTO {
    private LocalDate date;
    private Double avgProgression;
    private Double totalCost;

    // Default constructor
    public TrendDataDTO() {
    }

    // All-args constructor
    public TrendDataDTO(LocalDate date, Double avgProgression, Double totalCost) {
        this.date = date;
        this.avgProgression = avgProgression;
        this.totalCost = totalCost;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getAvgProgression() {
        return avgProgression;
    }

    public void setAvgProgression(Double avgProgression) {
        this.avgProgression = avgProgression;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }
}