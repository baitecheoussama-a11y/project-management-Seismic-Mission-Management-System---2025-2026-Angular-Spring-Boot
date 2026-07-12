package com.pfe.webapp.dto.project;

import java.time.LocalDate;
import java.util.List;

public class ProjectResponseDTO {
    private Long id;
    private String nom;
    private String description;
    private Double budget;
    private Double budgetDepense;
    private Integer objectifVP;
    private Integer vpAtteint;
    private LocalDate objectifDebut;
    private LocalDate objectifFin;
    private Integer progression;
    private Boolean annule;
    private Long missionId;
    private String missionCode;
    private LocalDate dateStartReelle;  // ✅ NEW
    private LocalDate dateFinReelle;
    private String status;  // ✅ NEW: Calculated status
    private List<RapportResponseDTO> rapports;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public Double getBudgetDepense() { return budgetDepense; }
    public void setBudgetDepense(Double budgetDepense) { this.budgetDepense = budgetDepense; }

    public Integer getObjectifVP() { return objectifVP; }
    public void setObjectifVP(Integer objectifVP) { this.objectifVP = objectifVP; }

    public Integer getVpAtteint() { return vpAtteint; }
    public void setVpAtteint(Integer vpAtteint) { this.vpAtteint = vpAtteint; }

    public LocalDate getObjectifDebut() { return objectifDebut; }
    public void setObjectifDebut(LocalDate objectifDebut) { this.objectifDebut = objectifDebut; }

    public LocalDate getObjectifFin() { return objectifFin; }
    public void setObjectifFin(LocalDate objectifFin) { this.objectifFin = objectifFin; }

    public Integer getProgression() { return progression; }
    public void setProgression(Integer progression) { this.progression = progression; }

    public Boolean getAnnule() { return annule; }
    public void setAnnule(Boolean annule) { this.annule = annule; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getMissionCode() { return missionCode; }
    public void setMissionCode(String missionCode) { this.missionCode = missionCode; }

    public LocalDate getDateStartReelle() { return dateStartReelle; }
    public void setDateStartReelle(LocalDate dateStartReelle) { this.dateStartReelle = dateStartReelle; }

    public LocalDate getDateFinReelle() { return dateFinReelle; }
    public void setDateFinReelle(LocalDate dateFinReelle) { this.dateFinReelle = dateFinReelle; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<RapportResponseDTO> getRapports() { return rapports; }
    public void setRapports(List<RapportResponseDTO> rapports) { this.rapports = rapports; }
}