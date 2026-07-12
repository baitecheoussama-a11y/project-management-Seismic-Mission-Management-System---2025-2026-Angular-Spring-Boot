package com.pfe.webapp.dto.team;

import java.time.LocalDate;

public class ActiveDTO {
    private Long id;
    private String codeActive;
    private String objectif;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double progression;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodeActive() { return codeActive; }
    public void setCodeActive(String codeActive) { this.codeActive = codeActive; }

    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Double getProgression() { return progression; }
    public void setProgression(Double progression) { this.progression = progression; }
}
