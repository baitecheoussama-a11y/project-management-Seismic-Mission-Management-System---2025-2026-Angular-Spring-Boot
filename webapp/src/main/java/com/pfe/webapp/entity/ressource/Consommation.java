package com.pfe.webapp.entity.ressource;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfe.webapp.entity.Mission;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "consommation")
public class Consommation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConsommation;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double valeur;  // Quantity consumed

    private String resume;  // Description/notes

    // Relations
    @ManyToOne
    @JoinColumn(name = "idRessource")
    @JsonIgnore
    private Ressource ressource;

    @ManyToOne
    @JoinColumn(name = "idMotif")
    @JsonIgnore
    private Motif motif;

    @ManyToOne
    @JoinColumn(name = "idContexte")
    @JsonIgnore
    private Contexte contexte;

    // ✅ NEW: Relation with Mission
    @ManyToOne
    @JoinColumn(name = "idMission")
    @JsonIgnore
    private Mission mission;

    // Constructors
    public Consommation() {}

    public Consommation(LocalDate date, Double valeur, String resume) {
        this.date = date;
        this.valeur = valeur;
        this.resume = resume;
    }

    // Getters and Setters
    public Long getIdConsommation() { return idConsommation; }
    public void setIdConsommation(Long idConsommation) { this.idConsommation = idConsommation; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getValeur() { return valeur; }
    public void setValeur(Double valeur) { this.valeur = valeur; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public Ressource getRessource() { return ressource; }
    public void setRessource(Ressource ressource) { this.ressource = ressource; }

    public Motif getMotif() { return motif; }
    public void setMotif(Motif motif) { this.motif = motif; }

    public Contexte getContexte() { return contexte; }
    public void setContexte(Contexte contexte) { this.contexte = contexte; }

    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }

    // ✅ Calculate total cost for this consumption
    @Transient
    public Double getTotalCost() {
        if (ressource != null && ressource.getCout() != null && valeur != null) {
            return valeur * ressource.getCout();
        }
        return 0.0;
    }
}