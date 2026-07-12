package com.pfe.webapp.dto.fonction;

public class FonctionSummaryDTO {
    private Long id;
    private String nom;
    private String description;

    public FonctionSummaryDTO() {}

    public FonctionSummaryDTO(Long id, String nom, String description) {
        this.id = id;
        this.nom = nom;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}