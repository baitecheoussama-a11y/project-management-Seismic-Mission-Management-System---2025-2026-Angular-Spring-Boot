// dto/analytics/ResourceCostDTO.java
package com.pfe.webapp.dto.analytics;

public class ResourceCostDTO {
    private Long resourceId;
    private String resourceName;
    private String unit;
    private Double totalCost;
    private Double totalQuantity;

    // Constructor
    public ResourceCostDTO() {}

    // Getters and Setters
    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public Double getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Double totalQuantity) { this.totalQuantity = totalQuantity; }
}