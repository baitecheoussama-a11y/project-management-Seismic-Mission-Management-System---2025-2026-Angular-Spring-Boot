package com.pfe.webapp.dto;

import java.time.LocalDate;
import java.util.List;

public class EmployeAccountDetailsDTO {
    // Employe Info
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String numTel;
    private String adresse;
    private String numIdentite;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String sexe;

    // Account Info
    private String username;
    private String compteStatus;

    // Contracts
    private List<ContratInfoDTO> contrats;

    // Medical Dossier
    private DossierMedicalInfoDTO dossierMedical;

    // Roles
    private List<RoleInfoDTO> roles;

    // ADD THIS LINE - Fonction field declaration
    private FonctionInfoDTO fonction;

    // Getters and Setters (جميع الحقول)
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

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getNumIdentite() { return numIdentite; }
    public void setNumIdentite(String numIdentite) { this.numIdentite = numIdentite; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getLieuNaissance() { return lieuNaissance; }
    public void setLieuNaissance(String lieuNaissance) { this.lieuNaissance = lieuNaissance; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getCompteStatus() { return compteStatus; }
    public void setCompteStatus(String compteStatus) { this.compteStatus = compteStatus; }

    public List<ContratInfoDTO> getContrats() { return contrats; }
    public void setContrats(List<ContratInfoDTO> contrats) { this.contrats = contrats; }

    public DossierMedicalInfoDTO getDossierMedical() { return dossierMedical; }
    public void setDossierMedical(DossierMedicalInfoDTO dossierMedical) { this.dossierMedical = dossierMedical; }

    public List<RoleInfoDTO> getRoles() { return roles; }
    public void setRoles(List<RoleInfoDTO> roles) { this.roles = roles; }

    // Add getter and setter for fonction
    public FonctionInfoDTO getFonction() {
        return fonction;
    }

    public void setFonction(FonctionInfoDTO fonction) {
        this.fonction = fonction;
    }

    // Inner class for Fonction info
    public static class FonctionInfoDTO {
        private Long id;
        private String nom;
        private String description;
        private Integer nombreEmployes;

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
    }
    // Inner DTOs
    public static class ContratInfoDTO {
        private Long id;
        private String type;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private String salaire;
        private String dureeTravail;
        private String regimeTravail;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public LocalDate getDateDebut() { return dateDebut; }
        public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
        public LocalDate getDateFin() { return dateFin; }
        public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
        public String getSalaire() { return salaire; }
        public void setSalaire(String salaire) { this.salaire = salaire; }
        public String getDureeTravail() { return dureeTravail; }
        public void setDureeTravail(String dureeTravail) { this.dureeTravail = dureeTravail; }
        public String getRegimeTravail() { return regimeTravail; }
        public void setRegimeTravail(String regimeTravail) { this.regimeTravail = regimeTravail; }


    }

    public static class DossierMedicalInfoDTO {
        private String groupeSanguin;
        private String antecedentsMedicaux;
        private String allergies;
        private String vaccinations;
        private String medicationsActuelles;
        private String medecinTraitant;
        private LocalDate derniereVisiteMedicale;

        // Getters and Setters
        public String getGroupeSanguin() { return groupeSanguin; }
        public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }
        public String getAntecedentsMedicaux() { return antecedentsMedicaux; }
        public void setAntecedentsMedicaux(String antecedentsMedicaux) { this.antecedentsMedicaux = antecedentsMedicaux; }
        public String getAllergies() { return allergies; }
        public void setAllergies(String allergies) { this.allergies = allergies; }
        public String getVaccinations() { return vaccinations; }
        public void setVaccinations(String vaccinations) { this.vaccinations = vaccinations; }
        public String getMedicationsActuelles() { return medicationsActuelles; }
        public void setMedicationsActuelles(String medicationsActuelles) { this.medicationsActuelles = medicationsActuelles; }
        public String getMedecinTraitant() { return medecinTraitant; }
        public void setMedecinTraitant(String medecinTraitant) { this.medecinTraitant = medecinTraitant; }
        public LocalDate getDerniereVisiteMedicale() { return derniereVisiteMedicale; }
        public void setDerniereVisiteMedicale(LocalDate derniereVisiteMedicale) { this.derniereVisiteMedicale = derniereVisiteMedicale; }
    }

    public static class RoleInfoDTO {
        private Long id;
        private String name;
        private String type;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private boolean active;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public LocalDate getDateDebut() { return dateDebut; }
        public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
        public LocalDate getDateFin() { return dateFin; }
        public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }
}