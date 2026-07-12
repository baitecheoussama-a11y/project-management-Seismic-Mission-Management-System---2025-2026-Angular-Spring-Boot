package com.pfe.webapp.dto;

import com.pfe.webapp.entity.StatusMateriel;
import java.time.LocalDate;
import java.util.List;

public class MaterielDTO {
    private Long idMateriel;
    private String codeMateriel;
    private String marque;
    private String modele;
    private String designation;
    private LocalDate dateAchat;
    private Double prix;
    private Integer quantityTotal;
    private Integer quantityAvailable;
    private StatusMateriel status;

    // Type information
    private Long typeMaterielId;
    private String typeMaterielLibelle;

    // Category information (from type)
    private Long categorieId;
    private String categorieNom;

    // Images
    private List<MaterielImageDTO> images;

    // Counts
    private int affectationCount;
    private int reparationCount;

    // Dynamic field for utilization status
    private boolean enUtilisation;

    // Total cost including repairs
    private Double coutTotalReparations;

    // Constructors
    public MaterielDTO() {}

    // Getters and Setters
    public Long getIdMateriel() { return idMateriel; }
    public void setIdMateriel(Long idMateriel) { this.idMateriel = idMateriel; }

    public String getCodeMateriel() { return codeMateriel; }
    public void setCodeMateriel(String codeMateriel) { this.codeMateriel = codeMateriel; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public LocalDate getDateAchat() { return dateAchat; }
    public void setDateAchat(LocalDate dateAchat) { this.dateAchat = dateAchat; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public Integer getQuantityTotal() { return quantityTotal; }
    public void setQuantityTotal(Integer quantityTotal) { this.quantityTotal = quantityTotal; }

    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public StatusMateriel getStatus() { return status; }
    public void setStatus(StatusMateriel status) { this.status = status; }

    public Long getTypeMaterielId() { return typeMaterielId; }
    public void setTypeMaterielId(Long typeMaterielId) { this.typeMaterielId = typeMaterielId; }

    public String getTypeMaterielLibelle() { return typeMaterielLibelle; }
    public void setTypeMaterielLibelle(String typeMaterielLibelle) { this.typeMaterielLibelle = typeMaterielLibelle; }

    public Long getCategorieId() { return categorieId; }
    public void setCategorieId(Long categorieId) { this.categorieId = categorieId; }

    public String getCategorieNom() { return categorieNom; }
    public void setCategorieNom(String categorieNom) { this.categorieNom = categorieNom; }

    public List<MaterielImageDTO> getImages() { return images; }
    public void setImages(List<MaterielImageDTO> images) { this.images = images; }

    public int getAffectationCount() { return affectationCount; }
    public void setAffectationCount(int affectationCount) { this.affectationCount = affectationCount; }

    public int getReparationCount() { return reparationCount; }
    public void setReparationCount(int reparationCount) { this.reparationCount = reparationCount; }

    public boolean isEnUtilisation() { return enUtilisation; }
    public void setEnUtilisation(boolean enUtilisation) { this.enUtilisation = enUtilisation; }

    public Double getCoutTotalReparations() { return coutTotalReparations; }
    public void setCoutTotalReparations(Double coutTotalReparations) { this.coutTotalReparations = coutTotalReparations; }
}