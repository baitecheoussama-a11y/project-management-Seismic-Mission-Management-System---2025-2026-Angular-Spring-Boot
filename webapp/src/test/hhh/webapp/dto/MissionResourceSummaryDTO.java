package com.pfe.webapp.dto;

import java.util.List;

public class MissionResourceSummaryDTO {
    private Double totalAllocated;
    private Double totalConsumed;
    private Double totalRemaining;
    private Double totalCost;
    private List<MissionResourceDTO> resources;

    // Getters and setters
    public Double getTotalAllocated() { return totalAllocated; }
    public void setTotalAllocated(Double totalAllocated) { this.totalAllocated = totalAllocated; }

    public Double getTotalConsumed() { return totalConsumed; }
    public void setTotalConsumed(Double totalConsumed) { this.totalConsumed = totalConsumed; }

    public Double getTotalRemaining() { return totalRemaining; }
    public void setTotalRemaining(Double totalRemaining) { this.totalRemaining = totalRemaining; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public List<MissionResourceDTO> getResources() { return resources; }
    public void setResources(List<MissionResourceDTO> resources) { this.resources = resources; }
}