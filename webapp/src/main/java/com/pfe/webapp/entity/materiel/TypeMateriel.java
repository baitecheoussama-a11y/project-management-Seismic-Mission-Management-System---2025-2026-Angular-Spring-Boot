package com.pfe.webapp.entity.materiel;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class TypeMateriel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTypeMateriel;

    private String libelle;

    @ManyToOne
    @JoinColumn(name = "idCategorie")
    private CategorieMateriel categorie;

    @OneToMany(mappedBy = "typeMateriel")
    private List<Materiel> materiels;

    // Getters and Setters
    public Long getIdTypeMateriel() { return idTypeMateriel; }
    public void setIdTypeMateriel(Long idTypeMateriel) { this.idTypeMateriel = idTypeMateriel; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public CategorieMateriel getCategorie() { return categorie; }
    public void setCategorie(CategorieMateriel categorie) { this.categorie = categorie; }

    public List<Materiel> getMateriels() { return materiels; }
    public void setMateriels(List<Materiel> materiels) { this.materiels = materiels; }
}