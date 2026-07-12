package com.pfe.webapp.entity.materiel;

import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "idReparation")
public class ReparationInterne extends Reparation {

    private String technicien;

    // Getters and Setters
    public String getTechnicien() { return technicien; }
    public void setTechnicien(String technicien) { this.technicien = technicien; }
}