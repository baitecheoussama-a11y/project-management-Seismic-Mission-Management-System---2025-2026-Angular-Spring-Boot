package com.pfe.webapp.dto;

import java.time.LocalDate;
import java.util.List;

public class BatchAffectationRequestDTO {
    private List<Long> materielIds;
    private Long missionId;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Getters and Setters
    public List<Long> getMaterielIds() { return materielIds; }
    public void setMaterielIds(List<Long> materielIds) { this.materielIds = materielIds; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}