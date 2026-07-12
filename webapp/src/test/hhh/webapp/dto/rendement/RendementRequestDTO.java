package com.pfe.webapp.dto.rendement;

import java.time.LocalDate;
import java.time.LocalTime;

public class RendementRequestDTO {
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private Double valeurRendement;
    private String uniteRendement;
    private LocalDate date;
    private Long activeId;  // ✅ ADD THIS - Activity ID

    // Constructors
    public RendementRequestDTO() {}

    public RendementRequestDTO(LocalTime heureDebut, LocalTime heureFin, Double valeurRendement,
                               String uniteRendement, LocalDate date, Long activeId) {
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.valeurRendement = valeurRendement;
        this.uniteRendement = uniteRendement;
        this.date = date;
        this.activeId = activeId;
    }

    // Getters and Setters
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

    public Long getActiveId() { return activeId; }
    public void setActiveId(Long activeId) { this.activeId = activeId; }
}