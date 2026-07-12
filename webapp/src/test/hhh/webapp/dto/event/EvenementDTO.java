package com.pfe.webapp.dto.event;

import java.time.LocalDate;
import java.time.LocalTime;

public class EvenementDTO {
    private Long id;
    private String titre;
    private String description;
    private LocalDate date;
    private LocalTime heure;
    private Long missionId;
    private String missionNom;
    private Long typeEvenementId;
    private String typeEvenementNom;
    private String niveauPriorite;
    private String niveauPrioriteLabel;
    private String niveauPrioriteColor;

    // Constructors
    public EvenementDTO() {}

    public EvenementDTO(Long id, String titre, String description, LocalDate date, LocalTime heure,
                        Long missionId, String missionNom, Long typeEvenementId,
                        String typeEvenementNom, String niveauPriorite,
                        String niveauPrioriteLabel, String niveauPrioriteColor) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.heure = heure;
        this.missionId = missionId;
        this.missionNom = missionNom;
        this.typeEvenementId = typeEvenementId;
        this.typeEvenementNom = typeEvenementNom;
        this.niveauPriorite = niveauPriorite;
        this.niveauPrioriteLabel = niveauPrioriteLabel;
        this.niveauPrioriteColor = niveauPrioriteColor;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getHeure() { return heure; }
    public void setHeure(LocalTime heure) { this.heure = heure; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getMissionNom() { return missionNom; }
    public void setMissionNom(String missionNom) { this.missionNom = missionNom; }

    public Long getTypeEvenementId() { return typeEvenementId; }
    public void setTypeEvenementId(Long typeEvenementId) { this.typeEvenementId = typeEvenementId; }

    public String getTypeEvenementNom() { return typeEvenementNom; }
    public void setTypeEvenementNom(String typeEvenementNom) { this.typeEvenementNom = typeEvenementNom; }

    public String getNiveauPriorite() { return niveauPriorite; }
    public void setNiveauPriorite(String niveauPriorite) { this.niveauPriorite = niveauPriorite; }

    public String getNiveauPrioriteLabel() { return niveauPrioriteLabel; }
    public void setNiveauPrioriteLabel(String niveauPrioriteLabel) { this.niveauPrioriteLabel = niveauPrioriteLabel; }

    public String getNiveauPrioriteColor() { return niveauPrioriteColor; }
    public void setNiveauPrioriteColor(String niveauPrioriteColor) { this.niveauPrioriteColor = niveauPrioriteColor; }
}