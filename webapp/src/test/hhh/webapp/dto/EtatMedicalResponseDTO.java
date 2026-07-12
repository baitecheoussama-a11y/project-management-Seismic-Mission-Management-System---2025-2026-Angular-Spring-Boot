package com.pfe.webapp.dto;

import java.time.LocalDate;
import java.util.List;

public class EtatMedicalResponseDTO {
    private Long id;
    private String groupeSanguin;
    private String allergies;
    private String vaccinations;
    private String medicationsActuelles;
    private String medecinTraitant;
    private LocalDate derniereVisiteMedicale;
    private Long employeId;
    private String employeNom;
    private String employePrenom;
    private List<AntecedentsMedicalDTO> antecedentsMedicaux;
    private Integer incidentCount;

    public EtatMedicalResponseDTO() {}

    public EtatMedicalResponseDTO(Long id, String groupeSanguin, String allergies, String vaccinations,
                                  String medicationsActuelles, String medecinTraitant,
                                  LocalDate derniereVisiteMedicale, Long employeId,
                                  String employeNom, String employePrenom) {
        this.id = id;
        this.groupeSanguin = groupeSanguin;
        this.allergies = allergies;
        this.vaccinations = vaccinations;
        this.medicationsActuelles = medicationsActuelles;
        this.medecinTraitant = medecinTraitant;
        this.derniereVisiteMedicale = derniereVisiteMedicale;
        this.employeId = employeId;
        this.employeNom = employeNom;
        this.employePrenom = employePrenom;
    }

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
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
    public String getEmployeNom() { return employeNom; }
    public void setEmployeNom(String employeNom) { this.employeNom = employeNom; }
    public String getEmployePrenom() { return employePrenom; }
    public void setEmployePrenom(String employePrenom) { this.employePrenom = employePrenom; }
    public List<AntecedentsMedicalDTO> getAntecedentsMedicaux() { return antecedentsMedicaux; }
    public void setAntecedentsMedicaux(List<AntecedentsMedicalDTO> antecedentsMedicaux) { this.antecedentsMedicaux = antecedentsMedicaux; }
    public Integer getIncidentCount() { return incidentCount; }
    public void setIncidentCount(Integer incidentCount) { this.incidentCount = incidentCount; }
}