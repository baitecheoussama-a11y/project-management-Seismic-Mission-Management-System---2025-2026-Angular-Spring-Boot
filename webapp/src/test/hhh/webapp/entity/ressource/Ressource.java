package com.pfe.webapp.entity.ressource;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ressource")
public class Ressource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRessource;

    @Column(nullable = false)
    private String titre;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double quantite;  // ✅ Stock quantity

    @Column(nullable = false)
    private String unite;     // ✅ Unit (kg, litre, piece, etc.)

    @Column(nullable = false)
    private Double cout;      // ✅ Cost per unit



    // ✅ Add this field
    private LocalDate dateAchat;

    @ManyToOne
    @JoinColumn(name = "idTypeRessource")
    @JsonBackReference
    private TypeRessource typeRessource;


    @OneToMany(mappedBy = "ressource", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Consommation> consommations = new ArrayList<>();

    // Constructors
    public Ressource() {}

    public Ressource(String titre, String description, Double quantite, String unite, Double cout) {
        this.titre = titre;
        this.description = description;
        this.quantite = quantite;
        this.unite = unite;
        this.cout = cout;
    }

    // Getters and Setters
    public Long getIdRessource() { return idRessource; }
    public void setIdRessource(Long idRessource) { this.idRessource = idRessource; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getQuantite() { return quantite; }
    public void setQuantite(Double quantite) { this.quantite = quantite; }

    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }

    public Double getCout() { return cout; }
    public void setCout(Double cout) { this.cout = cout; }

    public LocalDate getDateAchat() { return dateAchat; }
    public void setDateAchat(LocalDate dateAchat) { this.dateAchat = dateAchat; }


    public TypeRessource getTypeRessource() { return typeRessource; }
    public void setTypeRessource(TypeRessource typeRessource) { this.typeRessource = typeRessource; }

    public List<Consommation> getConsommations() { return consommations; }
    public void setConsommations(List<Consommation> consommations) { this.consommations = consommations; }

    // Helper methods
    public void addConsommation(Consommation consommation) {
        consommations.add(consommation);
        consommation.setRessource(this);
    }

    public void removeConsommation(Consommation consommation) {
        consommations.remove(consommation);
        consommation.setRessource(null);
    }

    // ✅ Calculate total cost of all consumptions
    @Transient
    public Double getTotalConsommationCost() {
        if (consommations == null || consommations.isEmpty()) {
            return 0.0;
        }
        return consommations.stream()
                .mapToDouble(c -> c.getValeur() * this.cout)
                .sum();
    }

    // ✅ Update stock quantity after consumption
    public void updateStockAfterConsumption(Double consumedQuantity) {
        if (this.quantite >= consumedQuantity) {
            this.quantite -= consumedQuantity;
        } else {
            throw new IllegalStateException("Insufficient stock. Available: " + this.quantite + ", Requested: " + consumedQuantity);
        }
    }
}