package com.pfe.webapp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pfe.webapp.entity.ressource.Consommation;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codeMission;

    @Enumerated(EnumType.STRING)
    private TypeMission methodologie;

    private String description;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL)
    private List<AffectationEmploye> affectations;

    @OneToMany(mappedBy = "mission")
    private List<Project> projects;

    // ✅ NEW: Relation with Consommation (without affecting existing services)
    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Consommation> consommations = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodeMission() { return codeMission; }
    public void setCodeMission(String codeMission) { this.codeMission = codeMission; }

    public TypeMission getMethodologie() { return methodologie; }
    public void setMethodologie(TypeMission methodologie) { this.methodologie = methodologie; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCode() { return codeMission; }
    public String getNom() { return "Mission " + codeMission; }

    public List<AffectationEmploye> getAffectations() { return affectations; }
    public void setAffectations(List<AffectationEmploye> affectations) { this.affectations = affectations; }

    public List<Project> getProjects() { return projects; }
    public void setProjects(List<Project> projects) { this.projects = projects; }

    // ✅ NEW: Getters and Setters for Consommations
    public List<Consommation> getConsommations() { return consommations; }
    public void setConsommations(List<Consommation> consommations) { this.consommations = consommations; }

    // ✅ NEW: Helper methods (optional - doesn't affect existing code)
    public void addConsommation(Consommation consommation) {
        consommations.add(consommation);
        consommation.setMission(this);
    }

    public void removeConsommation(Consommation consommation) {
        consommations.remove(consommation);
        consommation.setMission(null);
    }
}