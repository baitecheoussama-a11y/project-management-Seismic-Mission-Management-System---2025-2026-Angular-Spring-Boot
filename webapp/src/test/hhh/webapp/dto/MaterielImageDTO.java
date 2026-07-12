package com.pfe.webapp.dto;

public class MaterielImageDTO {
    private Long idImage;
    private String imageUrl;
    private String fileName;
    private String contentType;

    // Constructors
    public MaterielImageDTO() {}

    // Getters and Setters
    public Long getIdImage() { return idImage; }
    public void setIdImage(Long idImage) { this.idImage = idImage; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}