package com.pfe.webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Employe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String email;
    private String numTel;
    private String adresse;
    private String lieuNaissance;

    @Enumerated(EnumType.STRING)
    private SexeType sexe;

    private String numIdentite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fonction_id")
    private Fonction fonction;

    @OneToOne(mappedBy = "employe", cascade = CascadeType.ALL)
    private Compte compte;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL)
    private List<Contrat> contrats;

    @OneToOne(mappedBy = "employe", cascade = CascadeType.ALL)
    private EtatMedical etatMedical;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL)
    private List<Incident> incidents;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNumTel() { return numTel; }
    public void setNumTel(String numTel) { this.numTel = numTel; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getLieuNaissance() { return lieuNaissance; }
    public void setLieuNaissance(String lieuNaissance) { this.lieuNaissance = lieuNaissance; }

    public SexeType getSexe() { return sexe; }
    public void setSexe(SexeType sexe) { this.sexe = sexe; }

    public String getNumIdentite() { return numIdentite; }
    public void setNumIdentite(String numIdentite) { this.numIdentite = numIdentite; }

    public Fonction getFonction() { return fonction; }
    public void setFonction(Fonction fonction) { this.fonction = fonction; }

    public Compte getCompte() { return compte; }
    public void setCompte(Compte compte) { this.compte = compte; }

    public List<Contrat> getContrats() { return contrats; }
    public void setContrats(List<Contrat> contrats) { this.contrats = contrats; }

    public EtatMedical getEtatMedical() { return etatMedical; }
    public void setEtatMedical(EtatMedical etatMedical) { this.etatMedical = etatMedical; }

    public List<Incident> getIncidents() { return incidents; }
    public void setIncidents(List<Incident> incidents) { this.incidents = incidents; }
}