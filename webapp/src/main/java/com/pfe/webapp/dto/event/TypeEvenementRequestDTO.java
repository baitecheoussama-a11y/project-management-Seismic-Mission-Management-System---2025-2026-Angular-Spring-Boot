package com.pfe.webapp.dto.event;

public class TypeEvenementRequestDTO {

    private String nom;
    private String description;
    private String niveauPriorite;
    private Boolean actif;

    // Constructors
    public TypeEvenementRequestDTO() {}

    public TypeEvenementRequestDTO(String nom, String description, String niveauPriorite, Boolean actif) {
        this.nom = nom;
        this.description = description;
        this.niveauPriorite = niveauPriorite;
        this.actif = actif;
    }

    // Getters and Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNiveauPriorite() { return niveauPriorite; }
    public void setNiveauPriorite(String niveauPriorite) { this.niveauPriorite = niveauPriorite; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
}