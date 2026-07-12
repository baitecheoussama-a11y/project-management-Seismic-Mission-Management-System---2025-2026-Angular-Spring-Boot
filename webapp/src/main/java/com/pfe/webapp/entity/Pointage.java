package com.pfe.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Pointage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate datePointage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPointage status;

    private String motifAbsent;

    private String remarque;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    @JsonIgnore
    private Employe employe;

    // ========== Constructors ==========
    public Pointage() {}

    public Pointage(LocalDate datePointage, StatusPointage status, Employe employe) {
        this.datePointage = datePointage;
        this.status = status;
        this.employe = employe;
    }

    public Pointage(LocalDate datePointage, StatusPointage status, String motifAbsent, String remarque, Employe employe) {
        this.datePointage = datePointage;
        this.status = status;
        this.motifAbsent = motifAbsent;
        this.remarque = remarque;
        this.employe = employe;
    }

    // ========== Getters and Setters ==========
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDatePointage() {
        return datePointage;
    }

    public void setDatePointage(LocalDate datePointage) {
        this.datePointage = datePointage;
    }

    public StatusPointage getStatus() {
        return status;
    }

    public void setStatus(StatusPointage status) {
        this.status = status;
    }

    public String getMotifAbsent() {
        return motifAbsent;
    }

    public void setMotifAbsent(String motifAbsent) {
        this.motifAbsent = motifAbsent;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public Employe getEmploye() {
        return employe;
    }

    public void setEmploye(Employe employe) {
        this.employe = employe;
    }

    // ========== Helper Methods ==========
    @Transient
    public String getStatusLabel() {
        return status != null ? status.getLabel() : "Unknown";
    }

    @Transient
    public String getStatusColor() {
        return status != null ? status.getColor() : "#6b7280";
    }

    @Transient
    public boolean isPresent() {
        return status == StatusPointage.PRESENT;
    }

    @Transient
    public boolean isAbsent() {
        return status == StatusPointage.ABSENT;
    }

    @Transient
    public boolean isLate() {
        return status == StatusPointage.RETARD;
    }

    @Transient
    public boolean isOnLeave() {
        return status == StatusPointage.CONGE;
    }

    @Transient
    public boolean isOnMission() {
        return status == StatusPointage.MISSION;
    }
}