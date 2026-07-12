package com.pfe.webapp.entity.materiel;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name = "idReparation")
public class ReparationExterne extends Reparation {

    private String fournisseur;
    private LocalDate dateSortieChantier;
    private LocalDate dateEntreeChantier;

    // Getters and Setters
    public String getFournisseur() { return fournisseur; }
    public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }

    public LocalDate getDateSortieChantier() { return dateSortieChantier; }
    public void setDateSortieChantier(LocalDate dateSortieChantier) { this.dateSortieChantier = dateSortieChantier; }

    public LocalDate getDateEntreeChantier() { return dateEntreeChantier; }
    public void setDateEntreeChantier(LocalDate dateEntreeChantier) { this.dateEntreeChantier = dateEntreeChantier; }
}