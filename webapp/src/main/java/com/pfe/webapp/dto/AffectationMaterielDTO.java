package com.pfe.webapp.dto;

import java.time.LocalDate;
import java.util.List;

public class AffectationMaterielDTO {
    private Long idAffectation;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Long materielId;
    private String materielCode;
    private String materielMarque;
    private String materielModele;

    // Mission information
    private Long missionId;
    private String missionCode;
    private String missionNom;

    // Historiques
    private List<HistoriqueUtilisationDTO> historiques;

    // Constructors
    public AffectationMaterielDTO() {}

    // Getters and Setters
    public Long getIdAffectation() { return idAffectation; }
    public void setIdAffectation(Long idAffectation) { this.idAffectation = idAffectation; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Long getMaterielId() { return materielId; }
    public void setMaterielId(Long materielId) { this.materielId = materielId; }

    public String getMaterielCode() { return materielCode; }
    public void setMaterielCode(String materielCode) { this.materielCode = materielCode; }

    public String getMaterielMarque() { return materielMarque; }
    public void setMaterielMarque(String materielMarque) { this.materielMarque = materielMarque; }

    public String getMaterielModele() { return materielModele; }
    public void setMaterielModele(String materielModele) { this.materielModele = materielModele; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getMissionCode() { return missionCode; }
    public void setMissionCode(String missionCode) { this.missionCode = missionCode; }

    public String getMissionNom() { return missionNom; }
    public void setMissionNom(String missionNom) { this.missionNom = missionNom; }

    public List<HistoriqueUtilisationDTO> getHistoriques() { return historiques; }
    public void setHistoriques(List<HistoriqueUtilisationDTO> historiques) { this.historiques = historiques; }
}