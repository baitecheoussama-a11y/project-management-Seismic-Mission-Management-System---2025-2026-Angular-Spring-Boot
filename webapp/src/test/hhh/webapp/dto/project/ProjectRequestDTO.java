// dto/ProjectRequestDTO.java
package com.pfe.webapp.dto.project;

import java.time.LocalDate;

public class ProjectRequestDTO {
    private String nom;
    private String description;
    private Double budget;
    private Integer objectifVP;
    private LocalDate objectifDebut;
    private LocalDate objectifFin;
    private Long missionId;

    // Getters and Setters
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

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }
}



