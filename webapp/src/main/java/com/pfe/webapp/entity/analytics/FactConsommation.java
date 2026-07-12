// entity/analytics/FactConsommation.java
package com.pfe.webapp.entity.analytics;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "fact_consommation")
public class FactConsommation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long missionId;

    @Column(nullable = false)
    private Long resourceId;

    @Column(nullable = false)
    private Long timeId;

    // Measures
    private Double quantity;
    private Double unitCost;
    private Double totalCost;
    private LocalDate consommationDate;

    // Metadata
    private LocalDate calculatedDate;

    // Constructors
    public FactConsommation() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public Long getTimeId() { return timeId; }
    public void setTimeId(Long timeId) { this.timeId = timeId; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getUnitCost() { return unitCost; }
    public void setUnitCost(Double unitCost) { this.unitCost = unitCost; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public LocalDate getConsommationDate() { return consommationDate; }
    public void setConsommationDate(LocalDate consommationDate) { this.consommationDate = consommationDate; }

    public LocalDate getCalculatedDate() { return calculatedDate; }
    public void setCalculatedDate(LocalDate calculatedDate) { this.calculatedDate = calculatedDate; }
}