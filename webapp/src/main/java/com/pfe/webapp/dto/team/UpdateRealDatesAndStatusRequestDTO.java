// UpdateRealDatesAndStatusRequestDTO.java

package com.pfe.webapp.dto.team;

import java.time.LocalDate;

public class UpdateRealDatesAndStatusRequestDTO {
    private Long activeId;
    private Long missionId;
    private LocalDate dateStartReelle;
    private LocalDate dateFinReelle;

    // Getters and Setters
    public Long getActiveId() { return activeId; }
    public void setActiveId(Long activeId) { this.activeId = activeId; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public LocalDate getDateStartReelle() { return dateStartReelle; }
    public void setDateStartReelle(LocalDate dateStartReelle) { this.dateStartReelle = dateStartReelle; }

    public LocalDate getDateFinReelle() { return dateFinReelle; }
    public void setDateFinReelle(LocalDate dateFinReelle) { this.dateFinReelle = dateFinReelle; }
}