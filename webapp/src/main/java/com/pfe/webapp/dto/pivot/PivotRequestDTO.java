// src/main/java/com/pfe/webapp/dto/pivot/PivotRequestDTO.java
package com.pfe.webapp.dto.pivot;

public class PivotRequestDTO {
    private String rowField;
    private String colField;
    private String valueField;
    private String aggregator;
    private Long missionId;
    private String status;
    private Integer year;

    // Default Constructor
    public PivotRequestDTO() {
    }

    // All-args Constructor
    public PivotRequestDTO(String rowField, String colField, String valueField,
                           String aggregator, Long missionId, String status, Integer year) {
        this.rowField = rowField;
        this.colField = colField;
        this.valueField = valueField;
        this.aggregator = aggregator;
        this.missionId = missionId;
        this.status = status;
        this.year = year;
    }

    // Getters
    public String getRowField() {
        return rowField;
    }

    public String getColField() {
        return colField;
    }

    public String getValueField() {
        return valueField;
    }

    public String getAggregator() {
        return aggregator;
    }

    public Long getMissionId() {
        return missionId;
    }

    public String getStatus() {
        return status;
    }

    public Integer getYear() {
        return year;
    }

    // Setters
    public void setRowField(String rowField) {
        this.rowField = rowField;
    }

    public void setColField(String colField) {
        this.colField = colField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public void setAggregator(String aggregator) {
        this.aggregator = aggregator;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}