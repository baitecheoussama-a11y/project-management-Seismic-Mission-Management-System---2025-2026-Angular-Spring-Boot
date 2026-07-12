package com.pfe.webapp.dto;

import java.util.List;

public class SiteDTO {
    private Long id;
    private Double surface;
    private WilayaDTO wilaya;
    private List<CoordonneeDTO> coordonnees;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getSurface() { return surface; }
    public void setSurface(Double surface) { this.surface = surface; }

    public WilayaDTO getWilaya() { return wilaya; }
    public void setWilaya(WilayaDTO wilaya) { this.wilaya = wilaya; }

    public List<CoordonneeDTO> getCoordonnees() { return coordonnees; }
    public void setCoordonnees(List<CoordonneeDTO> coordonnees) { this.coordonnees = coordonnees; }
}