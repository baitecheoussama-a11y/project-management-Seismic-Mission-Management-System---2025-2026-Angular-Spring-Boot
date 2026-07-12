package com.pfe.webapp.dto;

import java.time.LocalDate;

public class ConsumptionRequestDTO {
    private Long resourceId;
    private Long missionId;
    private String motifCode;
    private String motifDescription;
    private String contexteTitle;
    private String contexteDescription;
    private Double quantity;
    private LocalDate date;
    private String description;

    // Getters and setters
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getMotifCode() { return motifCode; }
    public void setMotifCode(String motifCode) { this.motifCode = motifCode; }

    public String getMotifDescription() { return motifDescription; }
    public void setMotifDescription(String motifDescription) { this.motifDescription = motifDescription; }

    public String getContexteTitle() { return contexteTitle; }
    public void setContexteTitle(String contexteTitle) { this.contexteTitle = contexteTitle; }

    public String getContexteDescription() { return contexteDescription; }
    public void setContexteDescription(String contexteDescription) { this.contexteDescription = contexteDescription; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}