// dto/RapportResponseDTO.java
package com.pfe.webapp.dto.project;

import java.time.LocalDate;

public class RapportResponseDTO {
    private Long id;
    private String titre;
    private LocalDate date;
    private String resume;
    private Long projectId;

    // Constructors
    public RapportResponseDTO() {}

    public RapportResponseDTO(Long id, String titre, LocalDate date, String resume, Long projectId) {
        this.id = id;
        this.titre = titre;
        this.date = date;
        this.resume = resume;
        this.projectId = projectId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
}