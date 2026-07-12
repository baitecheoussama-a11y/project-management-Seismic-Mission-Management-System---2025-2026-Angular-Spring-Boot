package com.pfe.webapp.dto.team;

import java.time.LocalDate;
import java.time.LocalTime;

public class RendementDTO {
    private Long id;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Double valeurRendement;
    private String uniteRendement;
    private LocalDate date;
    private Double dureeHeures;

    // Getters and Setters
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

    public Double getDureeHeures() {
        if (heureDebut != null && heureFin != null) {
            return (double) (heureFin.getHour() - heureDebut.getHour()) +
                    (heureFin.getMinute() - heureDebut.getMinute()) / 60.0;
        }
        return dureeHeures;
    }
    public void setDureeHeures(Double dureeHeures) { this.dureeHeures = dureeHeures; }
}
