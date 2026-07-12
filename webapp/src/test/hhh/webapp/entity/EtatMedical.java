package com.pfe.webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class EtatMedical extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupeSanguin;
    private String allergies;
    private String vaccinations;
    private String medicationsActuelles;
    private String medecinTraitant;
    private LocalDate derniereVisiteMedicale;

    @OneToOne
    @JoinColumn(name = "employe_id", unique = true)
    private Employe employe;

    @OneToMany(mappedBy = "etatMedical", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AntecedentsMedical> antecedentsMedicaux = new ArrayList<>();

    @OneToMany(mappedBy = "etatMedical", cascade = CascadeType.ALL)
    private List<Incident> incidents = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }

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

    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }

    public List<AntecedentsMedical> getAntecedentsMedicaux() { return antecedentsMedicaux; }
    public void setAntecedentsMedicaux(List<AntecedentsMedical> antecedentsMedicaux) { this.antecedentsMedicaux = antecedentsMedicaux; }

    public List<Incident> getIncidents() { return incidents; }
    public void setIncidents(List<Incident> incidents) { this.incidents = incidents; }

    // Helper methods
    public void addAntecedent(AntecedentsMedical antecedent) {
        antecedentsMedicaux.add(antecedent);
        antecedent.setEtatMedical(this);
    }

    public void removeAntecedent(AntecedentsMedical antecedent) {
        antecedentsMedicaux.remove(antecedent);
        antecedent.setEtatMedical(null);
    }
}