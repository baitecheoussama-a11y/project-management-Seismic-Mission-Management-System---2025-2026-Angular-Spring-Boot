// src/main/java/com/pfe/webapp/dto/pivot/PivotRowDTO.java
package com.pfe.webapp.dto.pivot;

import java.util.Map;

public class PivotRowDTO {
    private String rowLabel;
    private Map<String, Double> values;
    private Double rowTotal;
    private Boolean isTotal;

    // Default Constructor
    public PivotRowDTO() {
    }

    // All-args Constructor
    public PivotRowDTO(String rowLabel, Map<String, Double> values,
                       Double rowTotal, Boolean isTotal) {
        this.rowLabel = rowLabel;
        this.values = values;
        this.rowTotal = rowTotal;
        this.isTotal = isTotal;
    }

    // Getters
    public String getRowLabel() {
        return rowLabel;
    }

    public Map<String, Double> getValues() {
        return values;
    }

    public Double getRowTotal() {
        return rowTotal;
    }

    public Boolean getIsTotal() {
        return isTotal;
    }

    // Setters
    public void setRowLabel(String rowLabel) {
        this.rowLabel = rowLabel;
    }

    public void setValues(Map<String, Double> values) {
        this.values = values;
    }

    public void setRowTotal(Double rowTotal) {
        this.rowTotal = rowTotal;
    }

    public void setIsTotal(Boolean isTotal) {
        this.isTotal = isTotal;
    }
}