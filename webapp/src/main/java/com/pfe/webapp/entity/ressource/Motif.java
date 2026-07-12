package com.pfe.webapp.entity.ressource;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "motif")
public class Motif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMotif;

    @Column(unique = true, nullable = false)
    private String code;

    private String description;

    @OneToMany(mappedBy = "motif", cascade = CascadeType.ALL, orphanRemoval = true)

    private List<Consommation> consommations = new ArrayList<>();

    // Constructors
    public Motif() {}

    public Motif(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // Getters and Setters
    public Long getIdMotif() { return idMotif; }
    public void setIdMotif(Long idMotif) { this.idMotif = idMotif; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Consommation> getConsommations() { return consommations; }
    public void setConsommations(List<Consommation> consommations) { this.consommations = consommations; }

    // Helper methods
    public void addConsommation(Consommation consommation) {
        consommations.add(consommation);
        consommation.setMotif(this);
    }

    public void removeConsommation(Consommation consommation) {
        consommations.remove(consommation);
        consommation.setMotif(null);
    }
}