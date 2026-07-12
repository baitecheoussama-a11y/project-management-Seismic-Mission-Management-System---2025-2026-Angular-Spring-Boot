// dto/EquipeRequestDTO.java
package com.pfe.webapp.dto;

import com.pfe.webapp.entity.TypeActivite;

public class EquipeRequestDTO {
    private String nom;
    private TypeActivite type;

    // Getters and Setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public TypeActivite getType() { return type; }
    public void setType(TypeActivite type) { this.type = type; }
}