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

    // ✅ NEW: Actual start and completion dates for this assignment
    private LocalDate dateStartReelle;
    private LocalDate dateFinReelle;

    // ✅ NEW: Order of the activity (for sorting)
    private Integer ordre;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @ManyToOne
    @JoinColumn(name = "active_id")
    @JsonIgnore
    private Active active;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

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

    // ========== Getters and Setters ==========
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

    public LocalDate getDateStartReelle() {
        return dateStartReelle;
    }

    public void setDateStartReelle(LocalDate dateStartReelle) {
        this.dateStartReelle = dateStartReelle;
    }

    public LocalDate getDateFinReelle() {
        return dateFinReelle;
    }

    public void setDateFinReelle(LocalDate dateFinReelle) {
        this.dateFinReelle = dateFinReelle;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

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

    // ========== Helper Methods ==========

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

    // Helper method to update dates based on status
    public void updateDatesFromStatus() {
        if (this.active != null && this.active.getEtatAvancements() != null) {
            EtatAvancement status = this.active.getEtatAvancements().stream()
                    .filter(e -> e.getActive() != null && e.getActive().getId().equals(this.active.getId()))
                    .findFirst()
                    .orElse(null);

            if (status != null && status.getStatus() != null) {
                StatusEtatAvancement currentStatus = status.getStatus();
                switch (currentStatus) {
                    case ENCOURS:
                        if (this.dateStartReelle == null) {
                            this.dateStartReelle = LocalDate.now();
                        }
                        break;
                    case TERMINI:
                        if (this.dateFinReelle == null) {
                            this.dateFinReelle = LocalDate.now();
                        }
                        if (this.dateStartReelle == null) {
                            this.dateStartReelle = LocalDate.now();
                        }
                        break;
                    case ANNULE:
                        if (this.dateFinReelle == null) {
                            this.dateFinReelle = LocalDate.now();
                        }
                        break;
                }
            }
        }
    }

    // Check if this assignment is active
    @Transient
    public boolean isActiveAssignment() {
        if (this.active != null && this.active.getEtatAvancements() != null) {
            EtatAvancement status = this.active.getEtatAvancements().stream()
                    .filter(e -> e.getActive() != null && e.getActive().getId().equals(this.active.getId()))
                    .findFirst()
                    .orElse(null);

            if (status != null && status.getStatus() != null) {
                StatusEtatAvancement currentStatus = status.getStatus();
                return currentStatus != StatusEtatAvancement.TERMINI
                        && currentStatus != StatusEtatAvancement.ANNULE;
            }
        }
        return false;
    }

    // Check if this assignment is completed
    @Transient
    public boolean isCompleted() {
        if (this.active != null && this.active.getEtatAvancements() != null) {
            EtatAvancement status = this.active.getEtatAvancements().stream()
                    .filter(e -> e.getActive() != null && e.getActive().getId().equals(this.active.getId()))
                    .findFirst()
                    .orElse(null);

            if (status != null && status.getStatus() != null) {
                return status.getStatus() == StatusEtatAvancement.TERMINI;
            }
        }
        return false;
    }
}