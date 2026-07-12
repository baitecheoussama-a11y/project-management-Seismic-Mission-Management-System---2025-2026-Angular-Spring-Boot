// dto/analytics/PivotRequestDTO.java
package com.pfe.webapp.dto.analytics;

import java.util.List;

public class PivotRequestDTO {
    private List<String> rows;        // Dimensions for rows (e.g., ["mission", "status"])
    private List<String> columns;     // Dimensions for columns (e.g., ["month"])
    private List<String> measures;    // Measures to aggregate (e.g., ["totalCost", "projectCount"])
    private String filter;            // Optional filter
    private String dateRange;         // Optional date range

    // Constructor
    public PivotRequestDTO() {}

    // Getters and Setters
    public List<String> getRows() {
        return rows;
    }

    public void setRows(List<String> rows) {
        this.rows = rows;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<String> getMeasures() {
        return measures;
    }

    public void setMeasures(List<String> measures) {
        this.measures = measures;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }
}