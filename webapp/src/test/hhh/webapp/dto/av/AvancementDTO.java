// dto/AvancementDTO.java
package com.pfe.webapp.dto.av;

import java.time.LocalDate;

public class AvancementDTO {
    private Long id;
    private String titre;
    private LocalDate date;
    private String resume;
    private Long etatAvancementId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public Long getEtatAvancementId() { return etatAvancementId; }
    public void setEtatAvancementId(Long etatAvancementId) { this.etatAvancementId = etatAvancementId; }
}

