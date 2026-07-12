// entity/Wilaya.java
package com.pfe.webapp.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Wilaya {

    @Id
    private Integer numWilaya;

    private String nom;

    private Double centerLatitude;

    private Double centerLongitude;

    @OneToMany(mappedBy = "wilaya")
    private List<Site> sites;

    // Getters and Setters
    public Integer getNumWilaya() { return numWilaya; }
    public void setNumWilaya(Integer numWilaya) { this.numWilaya = numWilaya; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Double getCenterLatitude() { return centerLatitude; }
    public void setCenterLatitude(Double centerLatitude) { this.centerLatitude = centerLatitude; }

    public Double getCenterLongitude() { return centerLongitude; }
    public void setCenterLongitude(Double centerLongitude) { this.centerLongitude = centerLongitude; }

    public List<Site> getSites() { return sites; }
    public void setSites(List<Site> sites) { this.sites = sites; }
}