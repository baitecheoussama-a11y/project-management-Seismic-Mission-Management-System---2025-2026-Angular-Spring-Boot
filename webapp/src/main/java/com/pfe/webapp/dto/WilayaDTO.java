
// dto/WilayaDTO.java
package com.pfe.webapp.dto;

public class WilayaDTO {
    private Integer numWilaya;
    private String nom;
    private Double centerLatitude;
    private Double centerLongitude;

    // Getters and Setters
    public Integer getNumWilaya() { return numWilaya; }
    public void setNumWilaya(Integer numWilaya) { this.numWilaya = numWilaya; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Double getCenterLatitude() { return centerLatitude; }
    public void setCenterLatitude(Double centerLatitude) { this.centerLatitude = centerLatitude; }

    public Double getCenterLongitude() { return centerLongitude; }
    public void setCenterLongitude(Double centerLongitude) { this.centerLongitude = centerLongitude; }
}