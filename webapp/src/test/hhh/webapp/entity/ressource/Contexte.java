package com.pfe.webapp.entity.ressource;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contexte")
public class Contexte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContexte;

    @Column(nullable = false)
    private String titre;

    private String description;

    @OneToMany(mappedBy = "contexte", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Consommation> consommations = new ArrayList<>();

    // Constructors
    public Contexte() {}

    public Contexte(String titre, String description) {
        this.titre = titre;
        this.description = description;
    }

    // Getters and Setters
    public Long getIdContexte() { return idContexte; }
    public void setIdContexte(Long idContexte) { this.idContexte = idContexte; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Consommation> getConsommations() { return consommations; }
    public void setConsommations(List<Consommation> consommations) { this.consommations = consommations; }

    // Helper methods
    public void addConsommation(Consommation consommation) {
        consommations.add(consommation);
        consommation.setContexte(this);
    }

    public void removeConsommation(Consommation consommation) {
        consommations.remove(consommation);
        consommation.setContexte(null);
    }
}