// src/main/java/com/pfe/webapp/dto/pivot/PivotResponseDTO.java
package com.pfe.webapp.dto.pivot;

import java.util.List;
import java.util.Map;

public class PivotResponseDTO {
    private List<String> headers;
    private List<PivotRowDTO> rows;
    private Map<String, Double> columnTotals;
    private Double grandTotal;
    private Integer totalRows;
    private Integer totalColumns;
    private String aggregator;
    private String valueField;
    private String rowField;
    private String colField;

    // Default Constructor
    public PivotResponseDTO() {
    }

    // All-args Constructor
    public PivotResponseDTO(List<String> headers, List<PivotRowDTO> rows,
                            Map<String, Double> columnTotals, Double grandTotal,
                            Integer totalRows, Integer totalColumns, String aggregator,
                            String valueField, String rowField, String colField) {
        this.headers = headers;
        this.rows = rows;
        this.columnTotals = columnTotals;
        this.grandTotal = grandTotal;
        this.totalRows = totalRows;
        this.totalColumns = totalColumns;
        this.aggregator = aggregator;
        this.valueField = valueField;
        this.rowField = rowField;
        this.colField = colField;
    }

    // Getters
    public List<String> getHeaders() {
        return headers;
    }

    public List<PivotRowDTO> getRows() {
        return rows;
    }

    public Map<String, Double> getColumnTotals() {
        return columnTotals;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public Integer getTotalColumns() {
        return totalColumns;
    }

    public String getAggregator() {
        return aggregator;
    }

    public String getValueField() {
        return valueField;
    }

    public String getRowField() {
        return rowField;
    }

    public String getColField() {
        return colField;
    }

    // Setters
    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public void setRows(List<PivotRowDTO> rows) {
        this.rows = rows;
    }

    public void setColumnTotals(Map<String, Double> columnTotals) {
        this.columnTotals = columnTotals;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public void setTotalColumns(Integer totalColumns) {
        this.totalColumns = totalColumns;
    }

    public void setAggregator(String aggregator) {
        this.aggregator = aggregator;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public void setRowField(String rowField) {
        this.rowField = rowField;
    }

    public void setColField(String colField) {
        this.colField = colField;
    }
}