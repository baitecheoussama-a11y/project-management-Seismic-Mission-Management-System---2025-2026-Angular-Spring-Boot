package com.pfe.webapp.entity.ressource;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categorie_ressource")
public class CategorieRessource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategorieRessource;

    @Column(unique = true, nullable = false)
    private String nom;

    @OneToMany(mappedBy = "categorieRessource", cascade = CascadeType.ALL, orphanRemoval = true) @JsonManagedReference
    private List<TypeRessource> types = new ArrayList<>();

    // Constructors
    public CategorieRessource() {}

    public CategorieRessource(String nom) {
        this.nom = nom;
    }

    // Getters and Setters
    public Long getIdCategorieRessource() { return idCategorieRessource; }
    public void setIdCategorieRessource(Long idCategorieRessource) { this.idCategorieRessource = idCategorieRessource; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public List<TypeRessource> getTypes() { return types; }
    public void setTypes(List<TypeRessource> types) { this.types = types; }

    // Helper methods
    public void addType(TypeRessource type) {
        types.add(type);
        type.setCategorieRessource(this);
    }

    public void removeType(TypeRessource type) {
        types.remove(type);
        type.setCategorieRessource(null);
    }
}