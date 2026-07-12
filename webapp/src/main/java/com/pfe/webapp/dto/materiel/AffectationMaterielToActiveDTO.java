package com.pfe.webapp.dto.materiel;

import java.time.LocalDate;

public class AffectationMaterielToActiveDTO {
    private Long idAffectation;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long materielId;
    private String materielCode;
    private String materielDesignation;
    private Long activeId;
    private String activeCode;
    private String activeObjectif;
    private Long projectId;
    private String projectNom;
    private Boolean isActive;

    // Constructors
    public AffectationMaterielToActiveDTO() {}

    // Getters and Setters
    public Long getIdAffectation() { return idAffectation; }
    public void setIdAffectation(Long idAffectation) { this.idAffectation = idAffectation; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Long getMaterielId() { return materielId; }
    public void setMaterielId(Long materielId) { this.materielId = materielId; }

    public String getMaterielCode() { return materielCode; }
    public void setMaterielCode(String materielCode) { this.materielCode = materielCode; }

    public String getMaterielDesignation() { return materielDesignation; }
    public void setMaterielDesignation(String materielDesignation) { this.materielDesignation = materielDesignation; }

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

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}