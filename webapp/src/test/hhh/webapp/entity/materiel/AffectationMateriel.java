package com.pfe.webapp.entity.materiel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pfe.webapp.entity.Mission;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class AffectationMateriel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAffectation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @ManyToOne
    @JoinColumn(name = "idMateriel")
    @JsonIgnore
    private Materiel materiel;

    @ManyToOne
    @JoinColumn(name = "idMission")
    @JsonIgnore
    private Mission mission;

    @OneToMany(mappedBy = "affectation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoriqueUtilisation> historiques;

    // Getters and Setters
    public Long getIdAffectation() { return idAffectation; }
    public void setIdAffectation(Long idAffectation) { this.idAffectation = idAffectation; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Materiel getMateriel() { return materiel; }
    public void setMateriel(Materiel materiel) { this.materiel = materiel; }

    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }

    public List<HistoriqueUtilisation> getHistoriques() { return historiques; }
    public void setHistoriques(List<HistoriqueUtilisation> historiques) { this.historiques = historiques; }
}