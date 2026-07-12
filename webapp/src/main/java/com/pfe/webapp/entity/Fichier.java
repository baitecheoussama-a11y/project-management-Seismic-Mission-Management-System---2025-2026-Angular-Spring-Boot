// src/main/java/com/pfe/webapp/entity/Fichier.java
package com.pfe.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Fichier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chemin;
    private String titre;

    @Enumerated(EnumType.STRING)
    private TypeFichier type;

    private LocalDateTime dateUpload;
    private Long taille; // Size in bytes

    @ManyToOne
    @JoinColumn(name = "rapport_id")
    @JsonIgnore
    private Rapport rapport;

    // Constructors
    public Fichier() {}

    public Fichier(String chemin, String titre, TypeFichier type, Rapport rapport) {
        this.chemin = chemin;
        this.titre = titre;
        this.type = type;
        this.rapport = rapport;
        this.dateUpload = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChemin() { return chemin; }
    public void setChemin(String chemin) { this.chemin = chemin; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public TypeFichier getType() { return type; }
    public void setType(TypeFichier type) { this.type = type; }

    public LocalDateTime getDateUpload() { return dateUpload; }
    public void setDateUpload(LocalDateTime dateUpload) { this.dateUpload = dateUpload; }

    public Long getTaille() { return taille; }
    public void setTaille(Long taille) { this.taille = taille; }

    public Rapport getRapport() { return rapport; }
    public void setRapport(Rapport rapport) { this.rapport = rapport; }
}