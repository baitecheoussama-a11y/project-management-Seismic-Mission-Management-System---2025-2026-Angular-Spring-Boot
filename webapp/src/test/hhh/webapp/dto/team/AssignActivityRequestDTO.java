package com.pfe.webapp.dto.team;
import java.time.LocalDate;

public class AssignActivityRequestDTO {
    private Long equipeId;
    private Long activeId;
    private Long missionId;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Getters and Setters
    public Long getEquipeId() { return equipeId; }
    public void setEquipeId(Long equipeId) { this.equipeId = equipeId; }

    public Long getActiveId() { return activeId; }
    public void setActiveId(Long activeId) { this.activeId = activeId; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}
