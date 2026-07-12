package com.pfe.webapp.dto.rapport;

import com.pfe.webapp.dto.rendement.RendementResponseDTO;
import java.time.LocalDate;
import java.util.List;

public class RapportResponseDTO {
    private Long id;
    private String titre;
    private LocalDate date;
    private String resume;
    private Long projectId;
    private String projectName;
    private String missionCode;
    private List<RendementResponseDTO> rendements;

    public RapportResponseDTO() {}

    public RapportResponseDTO(Long id, String titre, LocalDate date, String resume,
                              Long projectId, String projectName, String missionCode) {
        this.id = id;
        this.titre = titre;
        this.date = date;
        this.resume = resume;
        this.projectId = projectId;
        this.projectName = projectName;
        this.missionCode = missionCode;
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

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getMissionCode() { return missionCode; }
    public void setMissionCode(String missionCode) { this.missionCode = missionCode; }

    public List<RendementResponseDTO> getRendements() { return rendements; }
    public void setRendements(List<RendementResponseDTO> rendements) { this.rendements = rendements; }
}