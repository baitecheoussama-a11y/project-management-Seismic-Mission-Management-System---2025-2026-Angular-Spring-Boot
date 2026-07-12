// entity/Site.java
package com.pfe.webapp.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double surface;

    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "wilaya_id")
    private Wilaya wilaya;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coordonnee> coordonnees = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getSurface() { return surface; }
    public void setSurface(Double surface) { this.surface = surface; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public Wilaya getWilaya() { return wilaya; }
    public void setWilaya(Wilaya wilaya) { this.wilaya = wilaya; }

    public List<Coordonnee> getCoordonnees() { return coordonnees; }
    public void setCoordonnees(List<Coordonnee> coordonnees) { this.coordonnees = coordonnees; }
}