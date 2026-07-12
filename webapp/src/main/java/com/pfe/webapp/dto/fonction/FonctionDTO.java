// FonctionDTO.java
package com.pfe.webapp.dto.fonction;

import java.util.List;

public class FonctionDTO {
    private Long id;
    private String nom;
    private String description;
    private Integer nombreEmployes;
    private List<EmployeSummaryDTO> employes;

    public FonctionDTO() {}

    public FonctionDTO(Long id, String nom, String description, Integer nombreEmployes) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.nombreEmployes = nombreEmployes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNombreEmployes() {
        return nombreEmployes;
    }

    public void setNombreEmployes(Integer nombreEmployes) {
        this.nombreEmployes = nombreEmployes;
    }

    public List<EmployeSummaryDTO> getEmployes() {
        return employes;
    }

    public void setEmployes(List<EmployeSummaryDTO> employes) {
        this.employes = employes;
    }
}