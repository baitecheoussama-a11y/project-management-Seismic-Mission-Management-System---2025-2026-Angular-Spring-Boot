package com.pfe.webapp.dto;

import java.time.LocalDate;

public class FinReparationDTO {
    private Long reparationId;
    private LocalDate dateReparation;
    private Double cout;
    private LocalDate dateEntreeChantier; // for EXTERNE only

    // Getters and Setters
    public Long getReparationId() { return reparationId; }
    public void setReparationId(Long reparationId) { this.reparationId = reparationId; }

    public LocalDate getDateReparation() { return dateReparation; }
    public void setDateReparation(LocalDate dateReparation) { this.dateReparation = dateReparation; }

    public Double getCout() { return cout; }
    public void setCout(Double cout) { this.cout = cout; }

    public LocalDate getDateEntreeChantier() { return dateEntreeChantier; }
    public void setDateEntreeChantier(LocalDate dateEntreeChantier) { this.dateEntreeChantier = dateEntreeChantier; }
}