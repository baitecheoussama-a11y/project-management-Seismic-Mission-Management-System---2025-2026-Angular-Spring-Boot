// dto/RapportRequestDTO.java
package com.pfe.webapp.dto.project;

import java.time.LocalDate;

public class RapportRequestDTO {
    private String titre;
    private LocalDate date;
    private String resume;

    // Getters and Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
}
