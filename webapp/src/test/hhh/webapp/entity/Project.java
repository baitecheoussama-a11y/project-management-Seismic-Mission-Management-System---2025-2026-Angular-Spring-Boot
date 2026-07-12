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

    // Add progression field
    private Integer progression; // 0-100 percentage

    // ✅ NEW: Actual completion/cancellation date
    private LocalDate dateFinReelle; // Actual completion date (filled when status is TERMINI or ANNULE)

    @ManyToOne
    @JoinColumn(name = "mission_id")
    @JsonIgnore
    private Mission mission;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    private Site site;

    // ✅ NEW: One-to-Many with Rapport
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rapport> rapports = new ArrayList<>();

    // ✅ NEW: One-to-Many with EtatAvancement
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EtatAvancement> etatAvancements = new ArrayList<>();

    // ========== Constructors ==========
    public Project() {}

    public Project(String nom, String description, LocalDate objectifDebut, LocalDate objectifFin) {
        this.nom = nom;
        this.description = description;
        this.objectifDebut = objectifDebut;
        this.objectifFin = objectifFin;
        this.progression = 0;
        this.annule = false;
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

    public LocalDate getDateFinReelle() { return dateFinReelle; }
    public void setDateFinReelle(LocalDate dateFinReelle) { this.dateFinReelle = dateFinReelle; }

    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }

    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }

    public List<Rapport> getRapports() { return rapports; }
    public void setRapports(List<Rapport> rapports) { this.rapports = rapports; }

    public List<EtatAvancement> getEtatAvancements() { return etatAvancements; }
    public void setEtatAvancements(List<EtatAvancement> etatAvancements) { this.etatAvancements = etatAvancements; }

    // ========== Helper Methods ==========
    public void addRapport(Rapport rapport) {
        rapports.add(rapport);
        rapport.setProject(this);
    }

    public void removeRapport(Rapport rapport) {
        rapports.remove(rapport);
        rapport.setProject(null);
    }

    public void addEtatAvancement(EtatAvancement etatAvancement) {
        etatAvancements.add(etatAvancement);
        etatAvancement.setProject(this);
    }

    public void removeEtatAvancement(EtatAvancement etatAvancement) {
        etatAvancements.remove(etatAvancement);
        etatAvancement.setProject(null);
    }

    // Calculate progress based on dates if not manually set
// In Project.java - Update the calculateProgress method
    @Transient
    public int calculateProgress() {
        // If progression is manually set, use it
        if (progression != null && progression > 0) {
            return progression;
        }

        // Otherwise calculate based on dates
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

    // Check if project is active based on status
    @Transient
    public boolean isActive() {
        // First check if project is cancelled
        if (Boolean.TRUE.equals(annule)) {
            return false;
        }

        // Check project status from etatAvancements
        if (this.etatAvancements != null && !this.etatAvancements.isEmpty()) {
            Optional<EtatAvancement> projectStatus = this.etatAvancements.stream()
                    .filter(e -> e.getActive() == null)
                    .findFirst();

            if (projectStatus.isPresent()) {
                StatusEtatAvancement status = projectStatus.get().getStatus();
                // Project is active only if status is NOT TERMINI or ANNULE
                return status != null
                        && status != StatusEtatAvancement.TERMINI
                        && status != StatusEtatAvancement.ANNULE;
            }
        }

        // Fallback: check based on dates and progression
        return (objectifFin == null || !objectifFin.isBefore(LocalDate.now()))
                && (progression == null || progression < 100);
    }

    // Check if project is completed
    @Transient
    public boolean isCompleted() {
        return progression != null && progression >= 100;
    }

    // Get remaining days
    @Transient
    public long getRemainingDays() {
        if (objectifFin == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), objectifFin);
    }

    // Get formatted date range
    @Transient
    public String getDateRange() {
        String start = objectifDebut != null ? objectifDebut.toString() : "N/A";
        String end = objectifFin != null ? objectifFin.toString() : "N/A";
        return start + " - " + end;
    }

    // Method to update progression based on status AND set actual completion date
// Method to update progression based on status AND set actual completion date AND handle annulation
    public void updateProgressionFromStatus() {
        if (this.etatAvancements != null && !this.etatAvancements.isEmpty()) {
            Optional<EtatAvancement> projectStatus = this.etatAvancements.stream()
                    .filter(e -> e.getActive() == null)
                    .findFirst();

            if (projectStatus.isPresent()) {
                StatusEtatAvancement status = projectStatus.get().getStatus();
                if (status != null) {
                    switch (status) {
                        case PLANIFIER:
                            this.progression = 0;
                            this.annule = false; // Reset annule if status changes from cancelled
                            break;
                        case ENCOURS:
                            this.progression = 25;
                            this.annule = false; // Reset annule if status changes from cancelled
                            break;
                        case ENATTENTE:
                            this.progression = 50;
                            this.annule = false; // Reset annule if status changes from cancelled
                            break;
                        case ENRETARD:
                            this.progression = 60;
                            this.annule = false; // Reset annule if status changes from cancelled
                            break;
                        case TERMINI:
                            this.progression = 100;
                            this.annule = false; // Completed project is not cancelled
                            // ✅ Automatically set actual completion date
                            if (this.dateFinReelle == null) {
                                this.dateFinReelle = LocalDate.now();
                            }
                            break;
                        case ANNULE:
                            this.progression = 0;
                            this.annule = true; // ✅ Automatically set annule to true when cancelled
                            // ✅ Automatically set actual cancellation date
                            if (this.dateFinReelle == null) {
                                this.dateFinReelle = LocalDate.now();
                            }
                            break;
                    }
                }
            }
        }
    }

    // Check if project is delayed and update status automatically
    // Check if project is delayed and update status automatically
    @Transient
    public void checkAndUpdateDelayStatus() {
        // Only check if project is not already completed or cancelled
        if (this.etatAvancements == null || this.etatAvancements.isEmpty()) {
            return;
        }

        Optional<EtatAvancement> projectStatus = this.etatAvancements.stream()
                .filter(e -> e.getActive() == null)
                .findFirst();

        if (projectStatus.isEmpty()) {
            return;
        }

        EtatAvancement etat = projectStatus.get();
        StatusEtatAvancement currentStatus = etat.getStatus();

        // Don't change status if already TERMINI or ANNULE
        if (currentStatus == StatusEtatAvancement.TERMINI ||
                currentStatus == StatusEtatAvancement.ANNULE) {
            return;
        }

        // Don't change if already ENRETARD
        if (currentStatus == StatusEtatAvancement.ENRETARD) {
            return;
        }

        // Check if target end date is passed
        if (this.objectifFin != null && LocalDate.now().isAfter(this.objectifFin)) {
            // Auto-update to ENRETARD (Delayed)
            etat.setStatus(StatusEtatAvancement.ENRETARD);
            etat.setDateLastAvancement(LocalDate.now());

            // Update progression (this will also set annule = false if needed)
            this.updateProgressionFromStatus();
        }
    }

    // Check if project is delayed (without auto-updating)
    @Transient
    public boolean isDelayed() {
        if (Boolean.TRUE.equals(annule)) {
            return false;
        }

        // If already completed or cancelled, not delayed
        if (this.etatAvancements != null && !this.etatAvancements.isEmpty()) {
            Optional<EtatAvancement> projectStatus = this.etatAvancements.stream()
                    .filter(e -> e.getActive() == null)
                    .findFirst();

            if (projectStatus.isPresent()) {
                StatusEtatAvancement status = projectStatus.get().getStatus();
                if (status == StatusEtatAvancement.TERMINI || status == StatusEtatAvancement.ANNULE) {
                    return false;
                }
                if (status == StatusEtatAvancement.ENRETARD) {
                    return true;
                }
            }
        }

        // Check based on end date
        return this.objectifFin != null && LocalDate.now().isAfter(this.objectifFin);
    }



}