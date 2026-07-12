// src/main/java/com/pfe/webapp/dto/FichierUploadRequestDTO.java
package com.pfe.webapp.dto;

import com.pfe.webapp.entity.TypeFichier;

public class FichierUploadRequestDTO {
    private String titre;
    private TypeFichier type;
    private Long rapportId;

    // Default Constructor
    public FichierUploadRequestDTO() {}

    // All-args Constructor
    public FichierUploadRequestDTO(String titre, TypeFichier type, Long rapportId) {
        this.titre = titre;
        this.type = type;
        this.rapportId = rapportId;
    }

    // Getters and Setters
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public TypeFichier getType() { return type; }
    public void setType(TypeFichier type) { this.type = type; }

    public Long getRapportId() { return rapportId; }
    public void setRapportId(Long rapportId) { this.rapportId = rapportId; }
}