package com.pfe.webapp.dto;

import com.pfe.webapp.dto.fonction.FonctionSummaryDTO;
import com.pfe.webapp.entity.SexeType;
import com.pfe.webapp.entity.TypeContrat;
import java.time.LocalDate;
import java.math.BigDecimal;

public class EmployeDTO {
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

    // Informations du contrat
    private TypeContrat typeContrat;
    private LocalDate contratDateDebut;
    private LocalDate contratDateFin;
    private BigDecimal salaire;
    private String dureeTravail;
    private String regimeTravail;

    // Informations médicales
    private String groupeSanguin;
    private String antecedentsMedicaux;
    private String allergies;
    private String vaccinations;
    private String medicationsActuelles;
    private String medecinTraitant;
    private LocalDate derniereVisiteMedicale;
    private boolean isAvailable;
    private Long currentMissionId;
    private String currentMissionName;

    private String poste;
    private String photoUrl;

    // NEW: Fonction relationship
    private FonctionSummaryDTO fonction;  // Add this field

    private String fonctionNom;  // Add this
    private Long fonctionId;

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

    public TypeContrat getTypeContrat() { return typeContrat; }
    public void setTypeContrat(TypeContrat typeContrat) { this.typeContrat = typeContrat; }

    public LocalDate getContratDateDebut() { return contratDateDebut; }
    public void setContratDateDebut(LocalDate contratDateDebut) { this.contratDateDebut = contratDateDebut; }

    public LocalDate getContratDateFin() { return contratDateFin; }
    public void setContratDateFin(LocalDate contratDateFin) { this.contratDateFin = contratDateFin; }

    public BigDecimal getSalaire() { return salaire; }
    public void setSalaire(BigDecimal salaire) { this.salaire = salaire; }

    public String getDureeTravail() { return dureeTravail; }
    public void setDureeTravail(String dureeTravail) { this.dureeTravail = dureeTravail; }

    public String getRegimeTravail() { return regimeTravail; }
    public void setRegimeTravail(String regimeTravail) { this.regimeTravail = regimeTravail; }

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

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public Long getCurrentMissionId() { return currentMissionId; }
    public void setCurrentMissionId(Long currentMissionId) { this.currentMissionId = currentMissionId; }

    public String getCurrentMissionName() { return currentMissionName; }
    public void setCurrentMissionName(String currentMissionName) { this.currentMissionName = currentMissionName; }

    // NEW: Getter and Setter for fonction
    public FonctionSummaryDTO getFonction() { return fonction; }
    public void setFonction(FonctionSummaryDTO fonction) { this.fonction = fonction; }


    public String getFullName() { return prenom + " " + nom; }


    // Add these getters and setters
    public String getFonctionNom() {
        return fonctionNom;
    }

    public void setFonctionNom(String fonctionNom) {
        this.fonctionNom = fonctionNom;
    }

    public Long getFonctionId() {
        return fonctionId;
    }

    public void setFonctionId(Long fonctionId) {
        this.fonctionId = fonctionId;
    }

}