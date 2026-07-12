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
    private LocalDate dateStartReelle;
    private LocalDate dateFinReelle;
    private Long projectId;
    private String projectNom;
    private String status;
    private Integer ordre;  // ✅ NEW

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

    public LocalDate getDateStartReelle() { return dateStartReelle; }
    public void setDateStartReelle(LocalDate dateStartReelle) { this.dateStartReelle = dateStartReelle; }

    public LocalDate getDateFinReelle() { return dateFinReelle; }
    public void setDateFinReelle(LocalDate dateFinReelle) { this.dateFinReelle = dateFinReelle; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectNom() { return projectNom; }
    public void setProjectNom(String projectNom) { this.projectNom = projectNom; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
}