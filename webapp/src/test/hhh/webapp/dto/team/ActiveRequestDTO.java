package com.pfe.webapp.dto.team;

import java.time.LocalDate;

public class ActiveRequestDTO {
    private String codeActive;
    private String objectif;
    private String description;
    private Long missionId;  // Required for creation
    private LocalDate dateDebut;  // Start date for the assignment
    private LocalDate dateFin;    // End date for the assignment

    // Constructors
    public ActiveRequestDTO() {}

    public ActiveRequestDTO(String codeActive, String objectif, String description,
                            Long missionId, LocalDate dateDebut, LocalDate dateFin) {
        this.codeActive = codeActive;
        this.objectif = objectif;
        this.description = description;
        this.missionId = missionId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    // Getters and Setters
    public String getCodeActive() {
        return codeActive;
    }

    public void setCodeActive(String codeActive) {
        this.codeActive = codeActive;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
}