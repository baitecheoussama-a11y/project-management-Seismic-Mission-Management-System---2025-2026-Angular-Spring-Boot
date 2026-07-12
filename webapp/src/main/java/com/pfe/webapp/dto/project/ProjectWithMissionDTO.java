package com.pfe.webapp.dto.project;

import java.time.LocalDate;
import java.util.List;

public class ProjectWithMissionDTO {
    private Long id;
    private String nom;
    private String description;
    private Double budget;
    private Integer objectifVP;
    private LocalDate objectifDebut;
    private LocalDate objectifFin;
    private Integer progression;
    private Boolean annule;

    // ✅ Real dates for the project
    private LocalDate dateStartReelle;
    private LocalDate dateFinReelle;

    // ✅ Calculated status based on real dates
    private String status;

    // ✅ Mission data (not ignored)
    private Long missionId;
    private String missionCode;
    private String missionName;

    // ========== Getters and Setters ==========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public Integer getObjectifVP() { return objectifVP; }
    public void setObjectifVP(Integer objectifVP) { this.objectifVP = objectifVP; }

    public LocalDate getObjectifDebut() { return objectifDebut; }
    public void setObjectifDebut(LocalDate objectifDebut) { this.objectifDebut = objectifDebut; }

    public LocalDate getObjectifFin() { return objectifFin; }
    public void setObjectifFin(LocalDate objectifFin) { this.objectifFin = objectifFin; }

    public Integer getProgression() { return progression; }
    public void setProgression(Integer progression) { this.progression = progression; }

    public Boolean getAnnule() { return annule; }
    public void setAnnule(Boolean annule) { this.annule = annule; }

    public LocalDate getDateStartReelle() { return dateStartReelle; }
    public void setDateStartReelle(LocalDate dateStartReelle) { this.dateStartReelle = dateStartReelle; }

    public LocalDate getDateFinReelle() { return dateFinReelle; }
    public void setDateFinReelle(LocalDate dateFinReelle) { this.dateFinReelle = dateFinReelle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getMissionCode() { return missionCode; }
    public void setMissionCode(String missionCode) { this.missionCode = missionCode; }

    public String getMissionName() { return missionName; }
    public void setMissionName(String missionName) { this.missionName = missionName; }
}