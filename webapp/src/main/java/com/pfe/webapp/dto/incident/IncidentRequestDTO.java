package com.pfe.webapp.dto.incident;

import java.time.LocalDate;

public class IncidentRequestDTO {

    private String type;
    private String description;
    private LocalDate dateIncident;
    private String niveauGravite;
    private Long employeId;
    private CoordonneeIncidentRequestDTO coordonnee;

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateIncident() { return dateIncident; }
    public void setDateIncident(LocalDate dateIncident) { this.dateIncident = dateIncident; }

    public String getNiveauGravite() { return niveauGravite; }
    public void setNiveauGravite(String niveauGravite) { this.niveauGravite = niveauGravite; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public CoordonneeIncidentRequestDTO getCoordonnee() { return coordonnee; }
    public void setCoordonnee(CoordonneeIncidentRequestDTO coordonnee) { this.coordonnee = coordonnee; }
}