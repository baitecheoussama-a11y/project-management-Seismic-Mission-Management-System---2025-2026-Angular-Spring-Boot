// entity/analytics/DimResource.java
package com.pfe.webapp.entity.analytics;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_resource")
public class DimResource {

    @Id
    private Long id; // Same as Ressource.idRessource

    @Column(nullable = false)
    private String titre;

    private String unite;

    private String typeResource;

    // Constructors
    public DimResource() {}

    public DimResource(Long id, String titre, String unite, String typeResource) {
        this.id = id;
        this.titre = titre;
        this.unite = unite;
        this.typeResource = typeResource;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }

    public String getTypeResource() { return typeResource; }
    public void setTypeResource(String typeResource) { this.typeResource = typeResource; }
}