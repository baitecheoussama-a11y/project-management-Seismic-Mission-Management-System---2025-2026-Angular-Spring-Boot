package com.pfe.webapp.dto;

import java.time.LocalDate;

public class AntecedentsMedicalDTO {
    private Long id;
    private String nom;
    private String description;
    private LocalDate dateDiagnostic;
    private Long etatMedicalId;

    public AntecedentsMedicalDTO() {}

    public AntecedentsMedicalDTO(Long id, String nom, String description, LocalDate dateDiagnostic, Long etatMedicalId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateDiagnostic = dateDiagnostic;
        this.etatMedicalId = etatMedicalId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDateDiagnostic() { return dateDiagnostic; }
    public void setDateDiagnostic(LocalDate dateDiagnostic) { this.dateDiagnostic = dateDiagnostic; }
    public Long getEtatMedicalId() { return etatMedicalId; }
    public void setEtatMedicalId(Long etatMedicalId) { this.etatMedicalId = etatMedicalId; }
}