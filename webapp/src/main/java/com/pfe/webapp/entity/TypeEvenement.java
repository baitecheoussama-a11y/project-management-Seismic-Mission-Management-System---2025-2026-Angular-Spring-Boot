package com.pfe.webapp.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "type_evenements")
public class TypeEvenement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nom;

    private String description;

    @Enumerated(EnumType.STRING)
    private NiveauPriorite niveauPriorite;

    private Boolean actif = true;

    @OneToMany(mappedBy = "typeEvenement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evenement> evenements = new ArrayList<>();

    // Constructors
    public TypeEvenement() {}

    public TypeEvenement(String nom, String description, NiveauPriorite niveauPriorite) {
        this.nom = nom;
        this.description = description;
        this.niveauPriorite = niveauPriorite;
        this.actif = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public NiveauPriorite getNiveauPriorite() { return niveauPriorite; }
    public void setNiveauPriorite(NiveauPriorite niveauPriorite) { this.niveauPriorite = niveauPriorite; }

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }

    public List<Evenement> getEvenements() { return evenements; }
    public void setEvenements(List<Evenement> evenements) { this.evenements = evenements; }
}