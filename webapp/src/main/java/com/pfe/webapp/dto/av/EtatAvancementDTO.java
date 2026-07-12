// dto/EtatAvancementDTO.java
package com.pfe.webapp.dto.av;

import com.pfe.webapp.entity.StatusEtatAvancement;
import java.time.LocalDate;
import java.util.List;

public class EtatAvancementDTO {
    private Long id;
    private LocalDate dateLastAvancement;
    private StatusEtatAvancement status;
    private Long projectId;
    private String projectName;
    private Long activeId;
    private String activeCode;
    private List<AvancementDTO> avancements;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateLastAvancement() { return dateLastAvancement; }
    public void setDateLastAvancement(LocalDate dateLastAvancement) { this.dateLastAvancement = dateLastAvancement; }

    public StatusEtatAvancement getStatus() { return status; }
    public void setStatus(StatusEtatAvancement status) { this.status = status; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public Long getActiveId() { return activeId; }
    public void setActiveId(Long activeId) { this.activeId = activeId; }

    public String getActiveCode() { return activeCode; }
    public void setActiveCode(String activeCode) { this.activeCode = activeCode; }

    public List<AvancementDTO> getAvancements() { return avancements; }
    public void setAvancements(List<AvancementDTO> avancements) { this.avancements = avancements; }
}
