package com.pfe.webapp.dto;

import com.pfe.webapp.entity.SexeType;
import com.pfe.webapp.entity.TypeContrat;
import java.time.LocalDate;
import java.math.BigDecimal;

public class EmployeResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String email;
    private String numTel;
    private String adresse;
    private String lieuNaissance;
    private SexeType sexe;
    private String numIdentite;
    private TypeContrat typeContrat;
    private BigDecimal salaire;
    private String groupeSanguin;
    private String fonctionNom;  // Add this field
    private Long fonctionId;

    // Constructeur
    public EmployeResponseDTO(Long id, String nom, String prenom, LocalDate dateNaissance,
                              String email, String numTel, String adresse, String lieuNaissance,
                              SexeType sexe, String numIdentite, TypeContrat typeContrat,
                              BigDecimal salaire, String groupeSanguin) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.numTel = numTel;
        this.adresse = adresse;
        this.lieuNaissance = lieuNaissance;
        this.sexe = sexe;
        this.numIdentite = numIdentite;
        this.typeContrat = typeContrat;
        this.salaire = salaire;
        this.groupeSanguin = groupeSanguin;
    }

    // Getters seulement (pas de setters pour Response)
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getEmail() { return email; }
    public String getNumTel() { return numTel; }
    public String getAdresse() { return adresse; }
    public String getLieuNaissance() { return lieuNaissance; }
    public SexeType getSexe() { return sexe; }
    public String getNumIdentite() { return numIdentite; }
    public TypeContrat getTypeContrat() { return typeContrat; }
    public BigDecimal getSalaire() { return salaire; }
    public String getGroupeSanguin() { return groupeSanguin; }

    public String getFonctionNom() { return fonctionNom; }
    public void setFonctionNom(String fonctionNom) { this.fonctionNom = fonctionNom; }

    public Long getFonctionId() { return fonctionId; }
    public void setFonctionId(Long fonctionId) { this.fonctionId = fonctionId; }
}