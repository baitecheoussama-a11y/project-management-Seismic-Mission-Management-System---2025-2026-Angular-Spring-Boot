package com.pfe.webapp.dto;

import com.pfe.webapp.entity.NiveauGravite;
import com.pfe.webapp.entity.TypeIncident;
import java.time.LocalDate;

public class IncidentResponseDTO {
    private Long id;
    private TypeIncident type;
    private String description;
    private LocalDate dateIncident;
    private NiveauGravite niveauGravite;
    private Long employeId;
    private String employeNomComplet;
    private Long etatMedicalId;
    private CoordonneeDTO coordonnee;

    // Constructors
    public IncidentResponseDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TypeIncident getType() { return type; }
    public void setType(TypeIncident type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDateIncident() { return dateIncident; }
    public void setDateIncident(LocalDate dateIncident) { this.dateIncident = dateIncident; }
    public NiveauGravite getNiveauGravite() { return niveauGravite; }
    public void setNiveauGravite(NiveauGravite niveauGravite) { this.niveauGravite = niveauGravite; }
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public String getEmployeNomComplet() { return employeNomComplet; }
    public void setEmployeNomComplet(String employeNomComplet) { this.employeNomComplet = employeNomComplet; }
    public Long getEtatMedicalId() { return etatMedicalId; }
    public void setEtatMedicalId(Long etatMedicalId) { this.etatMedicalId = etatMedicalId; }
    public CoordonneeDTO getCoordonnee() { return coordonnee; }
    public void setCoordonnee(CoordonneeDTO coordonnee) { this.coordonnee = coordonnee; }
}