// dto/SiteRequestDTO.java
package com.pfe.webapp.dto;

import java.util.List;

public class SiteRequestDTO {
    private Long projectId;
    private Integer numWilaya;
    private Double surface;
    private List<CoordonneeDTO> coordonnees;

    // Getters and Setters
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Integer getNumWilaya() { return numWilaya; }
    public void setNumWilaya(Integer numWilaya) { this.numWilaya = numWilaya; }

    public Double getSurface() { return surface; }
    public void setSurface(Double surface) { this.surface = surface; }

    public List<CoordonneeDTO> getCoordonnees() { return coordonnees; }
    public void setCoordonnees(List<CoordonneeDTO> coordonnees) { this.coordonnees = coordonnees; }
}