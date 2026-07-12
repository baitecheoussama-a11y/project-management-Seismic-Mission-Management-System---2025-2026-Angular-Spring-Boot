package com.pfe.webapp.dto;

import java.time.LocalDate;

public class ReparationDTO {
    private Long idReparation;
    private Double cout;
    private LocalDate datePanne;
    private LocalDate dateReparation;
    private String detailProbleme;
    private Long materielId;
    private String materielCode;
    private String typeReparation; // "INTERNE" or "EXTERNE"

    // For ReparationExterne
    private String fournisseur;
    private LocalDate dateSortieChantier;
    private LocalDate dateEntreeChantier;

    // For ReparationInterne
    private String technicien;

    // Constructors
    public ReparationDTO() {}

    // Getters and Setters
    public Long getIdReparation() { return idReparation; }
    public void setIdReparation(Long idReparation) { this.idReparation = idReparation; }

    public Double getCout() { return cout; }
    public void setCout(Double cout) { this.cout = cout; }

    public LocalDate getDatePanne() { return datePanne; }
    public void setDatePanne(LocalDate datePanne) { this.datePanne = datePanne; }

    public LocalDate getDateReparation() { return dateReparation; }
    public void setDateReparation(LocalDate dateReparation) { this.dateReparation = dateReparation; }

    public String getDetailProbleme() { return detailProbleme; }
    public void setDetailProbleme(String detailProbleme) { this.detailProbleme = detailProbleme; }

    public Long getMaterielId() { return materielId; }
    public void setMaterielId(Long materielId) { this.materielId = materielId; }

    public String getMaterielCode() { return materielCode; }
    public void setMaterielCode(String materielCode) { this.materielCode = materielCode; }

    public String getTypeReparation() { return typeReparation; }
    public void setTypeReparation(String typeReparation) { this.typeReparation = typeReparation; }

    public String getFournisseur() { return fournisseur; }
    public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }

    public LocalDate getDateSortieChantier() { return dateSortieChantier; }
    public void setDateSortieChantier(LocalDate dateSortieChantier) { this.dateSortieChantier = dateSortieChantier; }

    public LocalDate getDateEntreeChantier() { return dateEntreeChantier; }
    public void setDateEntreeChantier(LocalDate dateEntreeChantier) { this.dateEntreeChantier = dateEntreeChantier; }

    public String getTechnicien() { return technicien; }
    public void setTechnicien(String technicien) { this.technicien = technicien; }
}