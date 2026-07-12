// entity/Rapport.java - أضف هذه العلاقة
package com.pfe.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Rapport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String titre;

    @Column(length = 5000)
    private String resume;

    @ManyToMany(mappedBy = "rapports")
    private List<AffectationEquipe> affectationEquipes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    // ✅ NEW: One-to-Many with Rendement
    @OneToMany(mappedBy = "rapport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rendement> rendements = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public List<AffectationEquipe> getAffectationEquipes() { return affectationEquipes; }
    public void setAffectationEquipes(List<AffectationEquipe> affectationEquipes) { this.affectationEquipes = affectationEquipes; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public List<Rendement> getRendements() { return rendements; }
    public void setRendements(List<Rendement> rendements) { this.rendements = rendements; }

    // Helper methods
    public void addRendement(Rendement rendement) {
        rendements.add(rendement);
        rendement.setRapport(this);
    }

    public void removeRendement(Rendement rendement) {
        rendements.remove(rendement);
        rendement.setRapport(null);
    }
}