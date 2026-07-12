package com.pfe.webapp.dto.team;

import java.time.LocalDate;

public class AffectationEquipeDTO {
    private Long id;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateStartReelle;
    private LocalDate dateFinReelle;
    private Integer ordre;  // ✅ NEW
    private Long equipeId;
    private String equipeNom;
    private Long activeId;
    private String activeCode;
    private String activeObjectif;
    private Long projectId;
    private String projectNom;
    private Long missionId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public LocalDate getDateStartReelle() { return dateStartReelle; }
    public void setDateStartReelle(LocalDate dateStartReelle) { this.dateStartReelle = dateStartReelle; }

    public LocalDate getDateFinReelle() { return dateFinReelle; }
    public void setDateFinReelle(LocalDate dateFinReelle) { this.dateFinReelle = dateFinReelle; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }

    public Long getEquipeId() { return equipeId; }
    public void setEquipeId(Long equipeId) { this.equipeId = equipeId; }

    public String getEquipeNom() { return equipeNom; }
    public void setEquipeNom(String equipeNom) { this.equipeNom = equipeNom; }

    public Long getActiveId() { return activeId; }
    public void setActiveId(Long activeId) { this.activeId = activeId; }

    public String getActiveCode() { return activeCode; }
    public void setActiveCode(String activeCode) { this.activeCode = activeCode; }

    public String getActiveObjectif() { return activeObjectif; }
    public void setActiveObjectif(String activeObjectif) { this.activeObjectif = activeObjectif; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectNom() { return projectNom; }
    public void setProjectNom(String projectNom) { this.projectNom = projectNom; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }
}