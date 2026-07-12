package com.pfe.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Rendement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Double valeurRendement;
    private String uniteRendement;
    private LocalDate date;

    // ✅ ADD THIS FIELD - Duration in hours
    private Double dureeHeures;

    @ManyToOne
    @JoinColumn(name = "affectation_equipe_id")
    @JsonIgnore
    private AffectationEquipe affectationEquipe;

    @ManyToOne
    @JoinColumn(name = "rapport_id")
    @JsonIgnore
    private Rapport rapport;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public Double getValeurRendement() { return valeurRendement; }
    public void setValeurRendement(Double valeurRendement) { this.valeurRendement = valeurRendement; }

    public String getUniteRendement() { return uniteRendement; }
    public void setUniteRendement(String uniteRendement) { this.uniteRendement = uniteRendement; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    // ✅ ADD THIS GETTER AND SETTER
    public Double getDureeHeures() { return dureeHeures; }
    public void setDureeHeures(Double dureeHeures) { this.dureeHeures = dureeHeures; }

    public AffectationEquipe getAffectationEquipe() { return affectationEquipe; }
    public void setAffectationEquipe(AffectationEquipe affectationEquipe) { this.affectationEquipe = affectationEquipe; }

    public Rapport getRapport() { return rapport; }
    public void setRapport(Rapport rapport) { this.rapport = rapport; }
}