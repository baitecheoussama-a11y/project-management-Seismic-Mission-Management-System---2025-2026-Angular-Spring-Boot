package com.pfe.webapp.entity.materiel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfe.webapp.entity.Active;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class AffectationMaterielToActive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAffectation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @ManyToOne
    @JoinColumn(name = "idMateriel")
    @JsonIgnore
    private Materiel materiel;

    @ManyToOne
    @JoinColumn(name = "active_id")
    @JsonIgnore
    private Active active;

    // ========== Constructors ==========
    public AffectationMaterielToActive() {}

    public AffectationMaterielToActive(Materiel materiel, Active active, LocalDate dateDebut) {
        this.materiel = materiel;
        this.active = active;
        this.dateDebut = dateDebut;
    }

    // ========== Getters and Setters ==========
    public Long getIdAffectation() {
        return idAffectation;
    }

    public void setIdAffectation(Long idAffectation) {
        this.idAffectation = idAffectation;
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

    public Materiel getMateriel() {
        return materiel;
    }

    public void setMateriel(Materiel materiel) {
        this.materiel = materiel;
    }

    public Active getActive() {
        return active;
    }

    public void setActive(Active active) {
        this.active = active;
    }

    // ========== Helper Methods ==========
    @Transient
    public boolean isActiveAssignment() {
        LocalDate today = LocalDate.now();
        return (dateDebut == null || !dateDebut.isAfter(today)) &&
                (dateFin == null || !dateFin.isBefore(today));
    }
}