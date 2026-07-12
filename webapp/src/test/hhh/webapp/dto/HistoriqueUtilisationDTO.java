package com.pfe.webapp.dto;

import java.time.LocalDate;

public class HistoriqueUtilisationDTO {
    private Long idUtilisation;
    private String resume;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Double valeurUtilisation;
    private Double uniteUtilisation;
    private Long affectationId;

    // Constructors
    public HistoriqueUtilisationDTO() {}

    // Getters and Setters
    public Long getIdUtilisation() { return idUtilisation; }
    public void setIdUtilisation(Long idUtilisation) { this.idUtilisation = idUtilisation; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Double getValeurUtilisation() { return valeurUtilisation; }
    public void setValeurUtilisation(Double valeurUtilisation) { this.valeurUtilisation = valeurUtilisation; }

    public Double getUniteUtilisation() { return uniteUtilisation; }
    public void setUniteUtilisation(Double uniteUtilisation) { this.uniteUtilisation = uniteUtilisation; }

    public Long getAffectationId() { return affectationId; }
    public void setAffectationId(Long affectationId) { this.affectationId = affectationId; }
}