package com.pfe.webapp.dto;

import java.util.List;

public class TypeMaterielDTO {
    private Long idTypeMateriel;
    private String libelle;
    private Long categorieId;
    private String categorieNom;
    private List<MaterielDTO> materiels;

    // Constructors
    public TypeMaterielDTO() {}

    public TypeMaterielDTO(Long idTypeMateriel, String libelle, Long categorieId, String categorieNom) {
        this.idTypeMateriel = idTypeMateriel;
        this.libelle = libelle;
        this.categorieId = categorieId;
        this.categorieNom = categorieNom;
    }

    // Getters and Setters
    public Long getIdTypeMateriel() { return idTypeMateriel; }
    public void setIdTypeMateriel(Long idTypeMateriel) { this.idTypeMateriel = idTypeMateriel; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public Long getCategorieId() { return categorieId; }
    public void setCategorieId(Long categorieId) { this.categorieId = categorieId; }

    public String getCategorieNom() { return categorieNom; }
    public void setCategorieNom(String categorieNom) { this.categorieNom = categorieNom; }

    public List<MaterielDTO> getMateriels() { return materiels; }
    public void setMateriels(List<MaterielDTO> materiels) { this.materiels = materiels; }
}