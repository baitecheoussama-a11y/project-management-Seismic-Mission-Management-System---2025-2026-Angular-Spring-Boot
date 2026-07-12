package com.pfe.webapp.dto.event;

public class TypeEvenementDTO {
    private Long id;
    private String nom;
    private String description;
    private String niveauPriorite;
    private String niveauPrioriteLabel;
    private String niveauPrioriteColor;
    private Boolean actif;
    private Integer evenementsCount;

    // Constructors
    public TypeEvenementDTO() {}

    public TypeEvenementDTO(Long id, String nom, String description, String niveauPriorite,
                            String niveauPrioriteLabel, String niveauPrioriteColor,
                            Boolean actif, Integer evenementsCount) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.niveauPriorite = niveauPriorite;
        this.niveauPrioriteLabel = niveauPrioriteLabel;
        this.niveauPrioriteColor = niveauPrioriteColor;
        this.actif = actif;
        this.evenementsCount = evenementsCount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNiveauPriorite() { return niveauPriorite; }
    public void setNiveauPriorite(String niveauPriorite) { this.niveauPriorite = niveauPriorite; }

    public String getNiveauPrioriteLabel() { return niveauPrioriteLabel; }
    public void setNiveauPrioriteLabel(String niveauPrioriteLabel) { this.niveauPrioriteLabel = niveauPrioriteLabel; }

    public String getNiveauPrioriteColor() { return niveauPrioriteColor; }
    public void setNiveauPrioriteColor(String niveauPrioriteColor) { this.niveauPrioriteColor = niveauPrioriteColor; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public Integer getEvenementsCount() { return evenementsCount; }
    public void setEvenementsCount(Integer evenementsCount) { this.evenementsCount = evenementsCount; }
}