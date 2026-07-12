// dto/team/EmployeSimpleDTO.java
package com.pfe.webapp.dto.team;

public class EmployeSimpleDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String numTel;
    private String poste;

    public EmployeSimpleDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNumTel() { return numTel; }
    public void setNumTel(String numTel) { this.numTel = numTel; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }
}