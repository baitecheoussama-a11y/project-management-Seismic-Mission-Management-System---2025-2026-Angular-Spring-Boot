package com.pfe.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AffectationEquipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @ManyToOne
    @JoinColumn(name = "active_id")
    @JsonIgnore
    private Active active;

    // ✅ ADD THIS - Relationship with Mission
    @ManyToOne
    @JoinColumn(name = "mission_id")
    @JsonIgnore
    private Mission mission;

    @ManyToMany
    @JoinTable(
            name = "affectation_equipe_rapport",
            joinColumns = @JoinColumn(name = "affectation_equipe_id"),
            inverseJoinColumns = @JoinColumn(name = "rapport_id")
    )
    private List<Rapport> rapports = new ArrayList<>();

    @OneToMany(mappedBy = "affectationEquipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rendement> rendements = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Equipe getEquipe() {
        return equipe;
    }

    public void setEquipe(Equipe equipe) {
        this.equipe = equipe;
    }

    public Active getActive() {
        return active;
    }

    public void setActive(Active active) {
        this.active = active;
    }

    // ✅ ADD GETTER AND SETTER FOR MISSION
    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public List<Rapport> getRapports() {
        return rapports;
    }

    public void setRapports(List<Rapport> rapports) {
        this.rapports = rapports;
    }

    public List<Rendement> getRendements() {
        return rendements;
    }

    public void setRendements(List<Rendement> rendements) {
        this.rendements = rendements;
    }

    // Helper methods for Rendement
    public void addRendement(Rendement rendement) {
        rendements.add(rendement);
        rendement.setAffectationEquipe(this);
    }

    public void removeRendement(Rendement rendement) {
        rendements.remove(rendement);
        rendement.setAffectationEquipe(null);
    }

    // Helper methods for Rapport
    public void addRapport(Rapport rapport) {
        rapports.add(rapport);
        rapport.getAffectationEquipes().add(this);
    }

    public void removeRapport(Rapport rapport) {
        rapports.remove(rapport);
        rapport.getAffectationEquipes().remove(this);
    }
}