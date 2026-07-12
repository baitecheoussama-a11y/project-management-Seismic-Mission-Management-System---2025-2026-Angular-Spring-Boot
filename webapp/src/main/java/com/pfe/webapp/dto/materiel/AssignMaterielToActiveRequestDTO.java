package com.pfe.webapp.dto.materiel;

import java.time.LocalDate;

public class AssignMaterielToActiveRequestDTO {
    private Long materielId;
    private Long activeId;
    private Long projectId;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Constructors
    public AssignMaterielToActiveRequestDTO() {}

    // Getters and Setters
    public Long getMaterielId() { return materielId; }
    public void setMaterielId(Long materielId) { this.materielId = materielId; }

    public Long getActiveId() { return activeId; }
    public void setActiveId(Long activeId) { this.activeId = activeId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}