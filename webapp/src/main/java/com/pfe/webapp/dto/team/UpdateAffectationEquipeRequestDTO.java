package com.pfe.webapp.dto.team;

import java.time.LocalDate;

public class UpdateAffectationEquipeRequestDTO {
    private LocalDate dateStartReelle;
    private LocalDate dateFinReelle;

    // Constructors
    public UpdateAffectationEquipeRequestDTO() {}

    public UpdateAffectationEquipeRequestDTO(LocalDate dateStartReelle, LocalDate dateFinReelle) {
        this.dateStartReelle = dateStartReelle;
        this.dateFinReelle = dateFinReelle;
    }

    // Getters and Setters
    public LocalDate getDateStartReelle() { return dateStartReelle; }
    public void setDateStartReelle(LocalDate dateStartReelle) { this.dateStartReelle = dateStartReelle; }

    public LocalDate getDateFinReelle() { return dateFinReelle; }
    public void setDateFinReelle(LocalDate dateFinReelle) { this.dateFinReelle = dateFinReelle; }
}