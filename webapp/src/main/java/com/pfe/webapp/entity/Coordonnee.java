// entity/Coordonnee.java
package com.pfe.webapp.entity;

import jakarta.persistence.*;

@Entity
public class Coordonnee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;
    private Integer ordre;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }

    public Site getSite() { return site; }
    public void setSite(Site site) { this.site = site; }
}