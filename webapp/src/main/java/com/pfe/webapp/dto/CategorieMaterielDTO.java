package com.pfe.webapp.dto;

import java.util.List;

public class CategorieMaterielDTO {
    private Long idCategorie;
    private String nom;
    private List<TypeMaterielDTO> types;

    // Constructors
    public CategorieMaterielDTO() {}

    public CategorieMaterielDTO(Long idCategorie, String nom) {
        this.idCategorie = idCategorie;
        this.nom = nom;
    }

    // Getters and Setters
    public Long getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Long idCategorie) { this.idCategorie = idCategorie; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public List<TypeMaterielDTO> getTypes() { return types; }
    public void setTypes(List<TypeMaterielDTO> types) { this.types = types; }
}