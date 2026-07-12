// src/main/java/com/pfe/webapp/dto/FichierDTO.java
package com.pfe.webapp.dto;

import com.pfe.webapp.entity.TypeFichier;
import java.time.LocalDateTime;

public class FichierDTO {
    private Long id;
    private String chemin;
    private String titre;
    private TypeFichier type;
    private LocalDateTime dateUpload;
    private Long taille;
    private Long rapportId;

    // Default Constructor
    public FichierDTO() {}

    // All-args Constructor
    public FichierDTO(Long id, String chemin, String titre, TypeFichier type,
                      LocalDateTime dateUpload, Long taille, Long rapportId) {
        this.id = id;
        this.chemin = chemin;
        this.titre = titre;
        this.type = type;
        this.dateUpload = dateUpload;
        this.taille = taille;
        this.rapportId = rapportId;
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

    public Long getRapportId() { return rapportId; }
    public void setRapportId(Long rapportId) { this.rapportId = rapportId; }
}