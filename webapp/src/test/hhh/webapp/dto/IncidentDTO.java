package com.pfe.webapp.dto;

import com.pfe.webapp.entity.NiveauGravite;
import com.pfe.webapp.entity.TypeIncident;
import java.time.LocalDate;

public class IncidentDTO {
    private Long id;
    private TypeIncident type;
    private String description;
    private LocalDate dateIncident;
    private NiveauGravite niveauGravite;
    private Long employeId;
    private Long etatMedicalId;
    private Long coordonneeId;

    // Constructors
    public IncidentDTO() {}

    public IncidentDTO(Long id, TypeIncident type, String description, LocalDate dateIncident,
                       NiveauGravite niveauGravite, Long employeId, Long etatMedicalId, Long coordonneeId) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.dateIncident = dateIncident;
        this.niveauGravite = niveauGravite;
        this.employeId = employeId;
        this.etatMedicalId = etatMedicalId;
        this.coordonneeId = coordonneeId;
    }

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
    public Long getEtatMedicalId() { return etatMedicalId; }
    public void setEtatMedicalId(Long etatMedicalId) { this.etatMedicalId = etatMedicalId; }
    public Long getCoordonneeId() { return coordonneeId; }
    public void setCoordonneeId(Long coordonneeId) { this.coordonneeId = coordonneeId; }
}