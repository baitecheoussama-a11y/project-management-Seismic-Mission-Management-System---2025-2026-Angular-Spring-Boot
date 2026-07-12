package com.pfe.webapp.dto.rapport;

import java.time.LocalDate;

public class RapportRequestDTO {
    private String titre;
    private LocalDate date;
    private String resume;

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
}