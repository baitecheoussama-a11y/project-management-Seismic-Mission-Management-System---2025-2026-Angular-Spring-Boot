// entity/Avancement.java
package com.pfe.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Avancement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private LocalDate date;

    @Column(length = 1000)
    private String resume;

    @ManyToOne
    @JoinColumn(name = "etat_avancement_id")
    @JsonIgnore
    private EtatAvancement etatAvancement;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }

    public EtatAvancement getEtatAvancement() { return etatAvancement; }
    public void setEtatAvancement(EtatAvancement etatAvancement) { this.etatAvancement = etatAvancement; }
}