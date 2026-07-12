// EmployeSummaryDTO.java
package com.pfe.webapp.dto.fonction;

public class EmployeSummaryDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String numIdentite;
    private String email;

    public EmployeSummaryDTO() {}

    public EmployeSummaryDTO(Long id, String nom, String prenom, String numIdentite, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.numIdentite = numIdentite;
        this.email = email;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNumIdentite() {
        return numIdentite;
    }

    public void setNumIdentite(String numIdentite) {
        this.numIdentite = numIdentite;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}