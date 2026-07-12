package com.pfe.webapp.dto.team;


import com.pfe.webapp.dto.FichierDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RapportDTO {
    private Long id;
    private LocalDate date;
    private String titre;
    private String resume;

    private Long projectId;
    private String projectName;
    private List<FichierDTO> fichiers = new ArrayList<>();
    private List<Long> rendementIds = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public List<FichierDTO> getFichiers() { return fichiers; }
    public void setFichiers(List<FichierDTO> fichiers) { this.fichiers = fichiers; }

    public List<Long> getRendementIds() { return rendementIds; }
    public void setRendementIds(List<Long> rendementIds) { this.rendementIds = rendementIds; }
}