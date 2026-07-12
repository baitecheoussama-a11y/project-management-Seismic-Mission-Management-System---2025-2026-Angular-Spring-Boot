package com.pfe.webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class AntecedentsMedical extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private LocalDate dateDiagnostic;

    @ManyToOne
    @JoinColumn(name = "etat_medical_id")
    private EtatMedical etatMedical;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateDiagnostic() { return dateDiagnostic; }
    public void setDateDiagnostic(LocalDate dateDiagnostic) { this.dateDiagnostic = dateDiagnostic; }

    public EtatMedical getEtatMedical() { return etatMedical; }
    public void setEtatMedical(EtatMedical etatMedical) { this.etatMedical = etatMedical; }
}