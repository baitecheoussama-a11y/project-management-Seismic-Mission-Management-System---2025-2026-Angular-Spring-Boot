package com.pfe.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Contrat extends BaseEntity {  // ✅ extends BaseEntity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeContrat type;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal salaire;
    private String dureeTravail;
    private String regimeTravail;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    @JsonIgnore
    private Employe employe;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TypeContrat getType() { return type; }
    public void setType(TypeContrat type) { this.type = type; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public BigDecimal getSalaire() { return salaire; }
    public void setSalaire(BigDecimal salaire) { this.salaire = salaire; }

    public String getDureeTravail() { return dureeTravail; }
    public void setDureeTravail(String dureeTravail) { this.dureeTravail = dureeTravail; }

    public String getRegimeTravail() { return regimeTravail; }
    public void setRegimeTravail(String regimeTravail) { this.regimeTravail = regimeTravail; }

    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
}