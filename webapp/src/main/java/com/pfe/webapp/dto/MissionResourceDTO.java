package com.pfe.webapp.dto;

import java.util.List;

public class MissionResourceDTO {
    private Long resourceId;
    private String resourceName;
    private Double totalAllocated;
    private Double totalConsumed;
    private Double remaining;
    private String unit;
    private Double costPerUnit;
    private List<ConsumptionDetailDTO> consumptions;

    // Getters and setters
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public Double getTotalAllocated() { return totalAllocated; }
    public void setTotalAllocated(Double totalAllocated) { this.totalAllocated = totalAllocated; }

    public Double getTotalConsumed() { return totalConsumed; }
    public void setTotalConsumed(Double totalConsumed) { this.totalConsumed = totalConsumed; }

    public Double getRemaining() { return remaining; }
    public void setRemaining(Double remaining) { this.remaining = remaining; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getCostPerUnit() { return costPerUnit; }
    public void setCostPerUnit(Double costPerUnit) { this.costPerUnit = costPerUnit; }

    public List<ConsumptionDetailDTO> getConsumptions() { return consumptions; }
    public void setConsumptions(List<ConsumptionDetailDTO> consumptions) { this.consumptions = consumptions; }
}