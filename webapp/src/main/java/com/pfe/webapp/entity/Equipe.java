package com.pfe.webapp.entity;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @Enumerated(EnumType.STRING)
    private TypeActivite type;

    @OneToMany(mappedBy = "equipe")
    private List<AffectationEmploye> affectations;


    // Add this field to Equipe class
    @ManyToMany
    @JoinTable(
            name = "equipe_active",
            joinColumns = @JoinColumn(name = "equipe_id"),
            inverseJoinColumns = @JoinColumn(name = "active_id")
    )
    private List<Active> actives = new ArrayList<>();

    // Add these getters and setters
    public List<Active> getActives() {
        return actives;
    }

    public void setActives(List<Active> actives) {
        this.actives = actives;
    }

    // Add helper methods
    public void addActive(Active active) {
        actives.add(active);
        active.getEquipes().add(this);
    }

    public void removeActive(Active active) {
        actives.remove(active);
        active.getEquipes().remove(this);
    }


    // Add this field to Equipe class
    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AffectationEquipe> affectationEquipes = new ArrayList<>();

    // Add getters and setters
    public List<AffectationEquipe> getAffectationEquipes() {
        return affectationEquipes;
    }

    public void setAffectationEquipes(List<AffectationEquipe> affectationEquipes) {
        this.affectationEquipes = affectationEquipes;
    }

    // Add helper method
    public void addAffectationEquipe(AffectationEquipe affectationEquipe) {
        affectationEquipes.add(affectationEquipe);
        affectationEquipe.setEquipe(this);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public TypeActivite getType() { return type; }
    public void setType(TypeActivite type) { this.type = type; }

    public List<AffectationEmploye> getAffectations() { return affectations; }
    public void setAffectations(List<AffectationEmploye> affectations) { this.affectations = affectations; }
}