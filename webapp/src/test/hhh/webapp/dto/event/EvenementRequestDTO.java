package com.pfe.webapp.dto.event;

import java.time.LocalDate;
import java.time.LocalTime;

public class EvenementRequestDTO {

    private String titre;
    private String description;
    private LocalDate date;
    private LocalTime heure;
    private Long missionId;
    private Long typeEvenementId;

    // Constructors
    public EvenementRequestDTO() {}

    public EvenementRequestDTO(String titre, String description, LocalDate date,
                               LocalTime heure, Long missionId, Long typeEvenementId) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.heure = heure;
        this.missionId = missionId;
        this.typeEvenementId = typeEvenementId;
    }

    // Getters and Setters
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public Long getTypeEvenementId() {
        return typeEvenementId;
    }

    public void setTypeEvenementId(Long typeEvenementId) {
        this.typeEvenementId = typeEvenementId;
    }
}