package com.pfe.webapp.entity.materiel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfe.webapp.entity.Mission;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Reparation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReparation;

    private LocalDate datePanne;
    private LocalDate dateReparation;
    private String detailProbleme;
    private Double cout;

    private String status; // PENDING, IN_PROGRESS, SENT, COMPLETED

    @ManyToOne
    @JoinColumn(name = "idMateriel")
    @JsonBackReference
    private Materiel materiel;

    @ManyToOne
    @JoinColumn(name = "idMission")
    @JsonIgnore
    private Mission mission;  // أي مهمة تتحمل العطل

    // Track if this breakdown is from stock or mission
    private String sourceType; // "STOCK" or "MISSION"

    // If from mission, which mission assignment
    private Long affectationId;

    // Getters and Setters
    public Long getIdReparation() { return idReparation; }
    public void setIdReparation(Long idReparation) { this.idReparation = idReparation; }

    public LocalDate getDatePanne() { return datePanne; }
    public void setDatePanne(LocalDate datePanne) { this.datePanne = datePanne; }

    public LocalDate getDateReparation() { return dateReparation; }
    public void setDateReparation(LocalDate dateReparation) { this.dateReparation = dateReparation; }

    public String getDetailProbleme() { return detailProbleme; }
    public void setDetailProbleme(String detailProbleme) { this.detailProbleme = detailProbleme; }

    public Double getCout() { return cout; }
    public void setCout(Double cout) { this.cout = cout; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Materiel getMateriel() { return materiel; }
    public void setMateriel(Materiel materiel) { this.materiel = materiel; }

    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public Long getAffectationId() { return affectationId; }
    public void setAffectationId(Long affectationId) { this.affectationId = affectationId; }
}