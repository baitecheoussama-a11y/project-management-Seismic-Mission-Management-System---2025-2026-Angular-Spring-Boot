// dto/AffectationRequestDTO.java
package com.pfe.webapp.dto;

import java.time.LocalDate;
import java.util.List;

public class AffectationRequestDTO {
    private Long missionId;
    private List<Long> employeIds;
    private Long equipeId;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Getters and Setters
    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public List<Long> getEmployeIds() { return employeIds; }
    public void setEmployeIds(List<Long> employeIds) { this.employeIds = employeIds; }

    public Long getEquipeId() { return equipeId; }
    public void setEquipeId(Long equipeId) { this.equipeId = equipeId; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}