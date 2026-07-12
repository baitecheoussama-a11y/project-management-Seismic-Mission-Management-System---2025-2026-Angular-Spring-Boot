package com.pfe.webapp.entity.materiel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pfe.webapp.entity.StatusMateriel;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Materiel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMateriel;

    private String codeMateriel;
    private String marque;
    private String modele;
    private String designation;
    private LocalDate dateAchat;
    private Double prix;

    @Enumerated(EnumType.STRING)
    private StatusMateriel status;

    @ManyToOne
    @JoinColumn(name = "idTypeMateriel")
    private TypeMateriel typeMateriel;

    @OneToMany(mappedBy = "materiel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaterielImage> images;

    @OneToMany(mappedBy = "materiel")
    @JsonIgnore
    private List<AffectationMateriel> affectations;

    @OneToMany(mappedBy = "materiel")
    @JsonManagedReference
    private List<Reparation> reparations;

    // ✅ NEW: Relationship with Active through AffectationMaterielToActive
    @OneToMany(mappedBy = "materiel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<AffectationMaterielToActive> affectationToActives = new ArrayList<>();

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

    public StatusMateriel getStatus() { return status; }
    public void setStatus(StatusMateriel status) { this.status = status; }

    public TypeMateriel getTypeMateriel() { return typeMateriel; }
    public void setTypeMateriel(TypeMateriel typeMateriel) { this.typeMateriel = typeMateriel; }

    public List<MaterielImage> getImages() { return images; }
    public void setImages(List<MaterielImage> images) { this.images = images; }

    public List<AffectationMateriel> getAffectations() { return affectations; }
    public void setAffectations(List<AffectationMateriel> affectations) { this.affectations = affectations; }

    public List<Reparation> getReparations() { return reparations; }
    public void setReparations(List<Reparation> reparations) { this.reparations = reparations; }


    // ========== Getters and Setters ==========

    public List<AffectationMaterielToActive> getAffectationToActives() {
        return affectationToActives;
    }

    public void setAffectationToActives(List<AffectationMaterielToActive> affectationToActives) {
        this.affectationToActives = affectationToActives;
    }

    // ========== Helper Methods ==========
    public void addAffectationToActive(AffectationMaterielToActive affectation) {
        affectationToActives.add(affectation);
        affectation.setMateriel(this);
    }

    public void removeAffectationToActive(AffectationMaterielToActive affectation) {
        affectationToActives.remove(affectation);
        affectation.setMateriel(null);
    }


    /**
     * LOGIQUE CORRIGÉE:
     * Un matériel est considéré "en utilisation" si:
     * 1. Il a une affectation active en cours
     */
    @Transient
    public boolean isEnUtilisation() {
        // Check if there's an active assignment
        LocalDate today = LocalDate.now();
        if (affectations == null) {
            return false;
        }

        return affectations.stream().anyMatch(a ->
                (a.getDateDebut() != null && !a.getDateDebut().isAfter(today)) &&
                        (a.getDateFin() == null || !a.getDateFin().isBefore(today))
        );
    }
}