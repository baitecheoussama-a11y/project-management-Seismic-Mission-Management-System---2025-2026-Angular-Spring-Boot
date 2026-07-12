package com.pfe.webapp.dto;

import java.time.LocalDate;

public class LancementReparationDTO {
    private Long reparationId;
    private String type; // INTERNE or EXTERNE
    private String technicien; // for INTERNE
    private String fournisseur; // for EXTERNE
    private LocalDate dateSortieChantier; // for EXTERNE

    // Getters and Setters
    public Long getReparationId() { return reparationId; }
    public void setReparationId(Long reparationId) { this.reparationId = reparationId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTechnicien() { return technicien; }
    public void setTechnicien(String technicien) { this.technicien = technicien; }

    public String getFournisseur() { return fournisseur; }
    public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }

    public LocalDate getDateSortieChantier() { return dateSortieChantier; }
    public void setDateSortieChantier(LocalDate dateSortieChantier) { this.dateSortieChantier = dateSortieChantier; }
}