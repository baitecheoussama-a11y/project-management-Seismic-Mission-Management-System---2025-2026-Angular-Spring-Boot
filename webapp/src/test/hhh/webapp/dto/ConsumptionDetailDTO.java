package com.pfe.webapp.dto;

import java.time.LocalDate;

public class ConsumptionDetailDTO {
    private Long id;
    private LocalDate date;
    private Double quantity;
    private String description;
    private String motifCode;
    private String motifDescription;
    private String contexteTitle;
    private String contexteDescription;
    private Double totalCost;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMotifCode() { return motifCode; }
    public void setMotifCode(String motifCode) { this.motifCode = motifCode; }

    public String getMotifDescription() { return motifDescription; }
    public void setMotifDescription(String motifDescription) { this.motifDescription = motifDescription; }

    public String getContexteTitle() { return contexteTitle; }
    public void setContexteTitle(String contexteTitle) { this.contexteTitle = contexteTitle; }

    public String getContexteDescription() { return contexteDescription; }
    public void setContexteDescription(String contexteDescription) { this.contexteDescription = contexteDescription; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }
}