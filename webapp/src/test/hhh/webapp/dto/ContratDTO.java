package com.pfe.webapp.dto;

import com.pfe.webapp.entity.TypeContrat;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ContratDTO {
    private TypeContrat type;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal salaire;
    private String dureeTravail;
    private String regimeTravail;
    private Long employeId;

    // Getters and Setters
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

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
}