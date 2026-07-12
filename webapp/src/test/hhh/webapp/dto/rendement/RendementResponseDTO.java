package com.pfe.webapp.dto.rendement;

import java.time.LocalDate;
import java.time.LocalTime;

public class RendementResponseDTO {
    private Long id;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Double valeurRendement;
    private String uniteRendement;
    private LocalDate date;
    private Double dureeHeures;
    private Long rapportId;
    private Long affectationEquipeId;
    private Long activeId;  // ✅ ADD THIS

    public RendementResponseDTO(Long id, LocalTime heureDebut, LocalTime heureFin, Double valeurRendement,
                                String uniteRendement, LocalDate date, Double dureeHeures,
                                Long rapportId, Long affectationEquipeId, Long activeId) {
        this.id = id;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.valeurRendement = valeurRendement;
        this.uniteRendement = uniteRendement;
        this.date = date;
        this.dureeHeures = dureeHeures;
        this.rapportId = rapportId;
        this.affectationEquipeId = affectationEquipeId;
        this.activeId = activeId;
    }

    // Getters and setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    public Double getValeurRendement() { return valeurRendement; }
    public void setValeurRendement(Double valeurRendement) { this.valeurRendement = valeurRendement; }
    public String getUniteRendement() { return uniteRendement; }
    public void setUniteRendement(String uniteRendement) { this.uniteRendement = uniteRendement; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Double getDureeHeures() { return dureeHeures; }
    public void setDureeHeures(Double dureeHeures) { this.dureeHeures = dureeHeures; }
    public Long getRapportId() { return rapportId; }
    public void setRapportId(Long rapportId) { this.rapportId = rapportId; }
    public Long getAffectationEquipeId() { return affectationEquipeId; }
    public void setAffectationEquipeId(Long affectationEquipeId) { this.affectationEquipeId = affectationEquipeId; }
    public Long getActiveId() { return activeId; }
    public void setActiveId(Long activeId) { this.activeId = activeId; }
}