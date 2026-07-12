package com.pfe.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfe.webapp.entity.materiel.AffectationMaterielToActive;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Active {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codeActive;

    private String objectif;

    private String description;

    @OneToMany(mappedBy = "active", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EtatAvancement> etatAvancements = new ArrayList<>();

    @ManyToMany(mappedBy = "actives")
    private List<Equipe> equipes = new ArrayList<>();

    @OneToMany(mappedBy = "active", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AffectationEquipe> affectationEquipes = new ArrayList<>();

    // ✅ NEW: Many-to-Many relationship with Materiel through AffectationMaterielToActive
    @OneToMany(mappedBy = "active", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AffectationMaterielToActive> affectationMateriels = new ArrayList<>();

    // ========== Getters and Setters ==========
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeActive() {
        return codeActive;
    }

    public void setCodeActive(String codeActive) {
        this.codeActive = codeActive;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EtatAvancement> getEtatAvancements() {
        return etatAvancements;
    }

    public void setEtatAvancements(List<EtatAvancement> etatAvancements) {
        this.etatAvancements = etatAvancements;
    }

    public List<Equipe> getEquipes() {
        return equipes;
    }

    public void setEquipes(List<Equipe> equipes) {
        this.equipes = equipes;
    }

    public List<AffectationEquipe> getAffectationEquipes() {
        return affectationEquipes;
    }

    public void setAffectationEquipes(List<AffectationEquipe> affectationEquipes) {
        this.affectationEquipes = affectationEquipes;
    }

    public List<AffectationMaterielToActive> getAffectationMateriels() {
        return affectationMateriels;
    }

    public void setAffectationMateriels(List<AffectationMaterielToActive> affectationMateriels) {
        this.affectationMateriels = affectationMateriels;
    }

    // ========== Helper Methods ==========

    // Helper methods for AffectationEquipe
    public void addAffectationEquipe(AffectationEquipe affectationEquipe) {
        affectationEquipes.add(affectationEquipe);
        affectationEquipe.setActive(this);
    }

    public void removeAffectationEquipe(AffectationEquipe affectationEquipe) {
        affectationEquipes.remove(affectationEquipe);
        affectationEquipe.setActive(null);
    }

    // Helper methods for AffectationMaterielToActive
    public void addAffectationMateriel(AffectationMaterielToActive affectationMateriel) {
        affectationMateriels.add(affectationMateriel);
        affectationMateriel.setActive(this);
    }

    public void removeAffectationMateriel(AffectationMaterielToActive affectationMateriel) {
        affectationMateriels.remove(affectationMateriel);
        affectationMateriel.setActive(null);
    }
}