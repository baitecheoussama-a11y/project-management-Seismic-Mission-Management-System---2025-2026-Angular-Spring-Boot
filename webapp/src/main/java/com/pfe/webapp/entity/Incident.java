package com.pfe.webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Incident extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeIncident type;

    private String description;
    private LocalDate dateIncident;

    @Enumerated(EnumType.STRING)
    private NiveauGravite niveauGravite;

    @ManyToOne
    @JoinColumn(name = "employe_id")
    private Employe employe;

    @ManyToOne
    @JoinColumn(name = "etat_medical_id")
    private EtatMedical etatMedical;

    @ManyToOne
    @JoinColumn(name = "coordonnee_id")
    private Coordonnee coordonnee;

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

    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }

    public EtatMedical getEtatMedical() { return etatMedical; }
    public void setEtatMedical(EtatMedical etatMedical) { this.etatMedical = etatMedical; }

    public Coordonnee getCoordonnee() { return coordonnee; }
    public void setCoordonnee(Coordonnee coordonnee) { this.coordonnee = coordonnee; }
}