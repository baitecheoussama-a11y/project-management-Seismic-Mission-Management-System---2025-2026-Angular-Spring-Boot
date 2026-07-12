package com.pfe.webapp.entity.materiel;

import jakarta.persistence.*;

@Entity
public class MaterielImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idImage;

    private String imageUrl;
    private String fileName;
    private String contentType;

    @ManyToOne
    @JoinColumn(name = "idMateriel")
    private Materiel materiel;

    // Getters and Setters
    public Long getIdImage() { return idImage; }
    public void setIdImage(Long idImage) { this.idImage = idImage; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Materiel getMateriel() { return materiel; }
    public void setMateriel(Materiel materiel) { this.materiel = materiel; }
}