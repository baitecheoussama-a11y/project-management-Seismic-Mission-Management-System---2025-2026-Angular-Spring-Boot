package com.pfe.webapp.dto.materiel;

import java.time.LocalDate;

public class UpdateMaterielToActiveRequestDTO {
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Constructors
    public UpdateMaterielToActiveRequestDTO() {}

    // Getters and Setters
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
}