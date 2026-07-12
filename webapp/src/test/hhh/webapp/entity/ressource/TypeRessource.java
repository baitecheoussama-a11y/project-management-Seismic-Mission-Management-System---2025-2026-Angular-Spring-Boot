package com.pfe.webapp.entity.ressource;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "type_ressource")
public class TypeRessource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTypeRessource;

    @Column(nullable = false)
    private String nom;

    @ManyToOne
    @JoinColumn(name = "idCategorieRessource")
    @JsonBackReference
    private CategorieRessource categorieRessource;

    @OneToMany(mappedBy = "typeRessource", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Ressource> ressources = new ArrayList<>();

    // Constructors
    public TypeRessource() {}

    public TypeRessource(String nom) {
        this.nom = nom;
    }

    // Getters and Setters
    public Long getIdTypeRessource() { return idTypeRessource; }
    public void setIdTypeRessource(Long idTypeRessource) { this.idTypeRessource = idTypeRessource; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public CategorieRessource getCategorieRessource() { return categorieRessource; }
    public void setCategorieRessource(CategorieRessource categorieRessource) { this.categorieRessource = categorieRessource; }

    public List<Ressource> getRessources() { return ressources; }
    public void setRessources(List<Ressource> ressources) { this.ressources = ressources; }

    // Helper methods
    public void addRessource(Ressource ressource) {
        ressources.add(ressource);
        ressource.setTypeRessource(this);
    }

    public void removeRessource(Ressource ressource) {
        ressources.remove(ressource);
        ressource.setTypeRessource(null);
    }
}