package com.pfe.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private Double budget;
    private Integer objectifVP;
    private LocalDate objectifDebut;
    private LocalDate objectifFin;
    private Boolean annule;

    private Integer progression; // 0-100 percentage

    // ✅ NEW: Actual dates for project
    private LocalDate dateStartReelle; // Actual start date
    private LocalDate dateFinReelle;   // Actual completion/cancellation date

    @ManyToOne
    @JoinColumn(name = "mission_id")
    @JsonIgnore
    private Mission mission;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    private Site site;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rapport> rapports = new ArrayList<>();

    // ✅ KEEP for compatibility - but NOT USED for status calculation
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EtatAvancement> etatAvancements = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AffectationEquipe> affectationEquipes = new ArrayList<>();

    // ========== Constructors ==========
    public Project() {}

    public Project(String nom, String description, LocalDate objectifDebut, LocalDate objectifFin) {
        this.nom = nom;
        this.description = description;
        this.objectifDebut = objectifDebut;
        this.objectifFin = objectifFin;
        this.progression = 0;
        this.annule = false;
        this.dateStartReelle = null;
        this.dateFinReelle = null;
    }

    // ========== Getters and Setters ==========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public Integer getObjectifVP() { return objectifVP; }
    public void setObjectifVP(Integer objectifVP) { this.objectifVP = objectifVP; }

    public LocalDate getObjectifDebut() { return objectifDebut; }
    public void setObjectifDebut(LocalDate objectifDebut) { this.objectifDebut = objectifDebut; }

    public LocalDate getObjectifFin() { return objectifFin; }
    public void setObjectifFin(LocalDate objectifFin) { this.objectifFin = objectifFin; }

    public Boolean getAnnule() { return annule; }
    public void setAnnule(Boolean annule) { this.annule = annule; }

    public Integer getProgression() { return progression; }
    public void setProgression(Integer progression) { this.progression = progression; }

    public LocalDate getDateStartReelle() { return dateStartReelle; }
    public void setDateStartReelle(LocalDate dateStartReelle) { this.dateStartReelle = dateStartReelle; }

    public LocalDate getDateFinReelle() { return dateFinReelle; }
    public void setDateFinReelle(LocalDate dateFinReelle) { this.dateFinReelle = dateFinReelle; }

    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }

    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }

    public List<Rapport> getRapports() { return rapports; }
    public void setRapports(List<Rapport> rapports) { this.rapports = rapports; }

    // ✅ KEEP for compatibility
    public List<EtatAvancement> getEtatAvancements() { return etatAvancements; }
    public void setEtatAvancements(List<EtatAvancement> etatAvancements) { this.etatAvancements = etatAvancements; }

    public List<AffectationEquipe> getAffectationEquipes() {
        return affectationEquipes;
    }

    public void setAffectationEquipes(List<AffectationEquipe> affectationEquipes) {
        this.affectationEquipes = affectationEquipes;
    }

    // ========== Helper Methods ==========
    public void addAffectationEquipe(AffectationEquipe affectationEquipe) {
        affectationEquipes.add(affectationEquipe);
        affectationEquipe.setProject(this);
    }

    public void removeAffectationEquipe(AffectationEquipe affectationEquipe) {
        affectationEquipes.remove(affectationEquipe);
        affectationEquipe.setProject(null);
    }

    public void addRapport(Rapport rapport) {
        rapports.add(rapport);
        rapport.setProject(this);
    }

    public void removeRapport(Rapport rapport) {
        rapports.remove(rapport);
        rapport.setProject(null);
    }

    // ✅ KEEP for compatibility - but don't use it for status
    public void addEtatAvancement(EtatAvancement etatAvancement) {
        etatAvancements.add(etatAvancement);
        etatAvancement.setProject(this);
    }

    public void removeEtatAvancement(EtatAvancement etatAvancement) {
        etatAvancements.remove(etatAvancement);
        etatAvancement.setProject(null);
    }

    // ✅ FIXED: Calculate status based on dates and annule flag
    @Transient
    public String calculateStatus() {
        LocalDate now = LocalDate.now();

        // FIRST: Check if project is cancelled via annule flag
        if (annule != null && annule) {
            return "ANNULE";
        }

        // Case 1: No dates at all -> PLANNED (not cancelled)
        if (dateStartReelle == null && dateFinReelle == null && objectifDebut == null && objectifFin == null) {
            return "PLANIFIER";
        }

        // Case 2: dateFinReelle is set -> COMPLETED
        if (dateFinReelle != null) {
            return "TERMINI";
        }

        // Case 3: dateStartReelle is set
        if (dateStartReelle != null) {
            // If dateStartReelle is in the future -> ON HOLD
            if (dateStartReelle.isAfter(now)) {
                return "ENATTENTE";
            }
            // If dateStartReelle is today or past -> IN PROGRESS
            // Check if delayed (target end date passed)
            if (objectifFin != null && now.isAfter(objectifFin)) {
                return "ENRETARD";
            }
            return "ENCOURS";
        }

        // Case 4: No dateStartReelle but has objectifDebut and/or objectifFin -> PLANNED
        return "PLANIFIER";
    }

    // ✅ Calculate progression based on status
    @Transient
    public int calculateProgressionFromStatus() {
        String status = calculateStatus();
        switch (status) {
            case "TERMINI":
                return 100;
            case "ENCOURS":
                return 50;
            case "ENRETARD":
                return 40;
            case "ENATTENTE":
                return 25;
            case "PLANIFIER":
                return 0;
            case "ANNULE":
                return 0;
            default:
                return 0;
        }
    }

    // Calculate progress based on dates if not manually set
    @Transient
    public int calculateProgress() {
        // If we have a status from real dates, use it
        if (dateStartReelle != null || dateFinReelle != null) {
            return calculateProgressionFromStatus();
        }

        // Otherwise calculate based on planned dates
        if (objectifDebut == null || objectifFin == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();

        if (today.isBefore(objectifDebut)) {
            return 0;
        }

        if (today.isAfter(objectifFin)) {
            return 100;
        }

        long totalDays = ChronoUnit.DAYS.between(objectifDebut, objectifFin);
        long daysPassed = ChronoUnit.DAYS.between(objectifDebut, today);

        if (totalDays <= 0) return 0;

        return (int) ((daysPassed * 100) / totalDays);
    }

    // Check if project is completed
    @Transient
    public boolean isCompleted() {
        return "TERMINI".equals(calculateStatus());
    }

    // Check if project is cancelled
    @Transient
    public boolean isCancelled() {
        return "ANNULE".equals(calculateStatus());
    }

    // Get remaining days
    @Transient
    public long getRemainingDays() {
        if (objectifFin == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), objectifFin);
    }

    // Check if project is delayed
    @Transient
    public boolean isDelayed() {
        if (isCompleted() || isCancelled()) {
            return false;
        }
        return objectifFin != null && LocalDate.now().isAfter(objectifFin);
    }
}