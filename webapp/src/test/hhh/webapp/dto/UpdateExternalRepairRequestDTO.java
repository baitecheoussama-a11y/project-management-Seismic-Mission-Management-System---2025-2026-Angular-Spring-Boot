package com.pfe.webapp.dto;

import java.time.LocalDate;

public class UpdateExternalRepairRequestDTO {
    private String fournisseur;
    private Integer quantity;
    private String detailProbleme;
    private LocalDate dateSortieChantier;

    // Getters and Setters
    public String getFournisseur() { return fournisseur; }
    public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getDetailProbleme() { return detailProbleme; }
    public void setDetailProbleme(String detailProbleme) { this.detailProbleme = detailProbleme; }

    public LocalDate getDateSortieChantier() { return dateSortieChantier; }
    public void setDateSortieChantier(LocalDate dateSortieChantier) { this.dateSortieChantier = dateSortieChantier; }
}