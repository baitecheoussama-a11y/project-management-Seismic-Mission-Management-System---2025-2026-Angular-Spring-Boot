package com.pfe.webapp.dto;

import com.pfe.webapp.entity.TypeContrat;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ContratResponseDTO {
    private Long id;
    private TypeContrat type;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal salaire;
    private String dureeTravail;
    private String regimeTravail;
    private String employeNom;
    private String employePrenom;
    private Long employeId;

    public ContratResponseDTO(Long id, TypeContrat type, LocalDate dateDebut, LocalDate dateFin,
                              BigDecimal salaire, String dureeTravail, String regimeTravail,
                              String employeNom, String employePrenom, Long employeId) {
        this.id = id;
        this.type = type;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.salaire = salaire;
        this.dureeTravail = dureeTravail;
        this.regimeTravail = regimeTravail;
        this.employeNom = employeNom;
        this.employePrenom = employePrenom;
        this.employeId = employeId;
    }

    // Getters
    public Long getId() { return id; }
    public TypeContrat getType() { return type; }
    public LocalDate getDateDebut() { return dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public BigDecimal getSalaire() { return salaire; }
    public String getDureeTravail() { return dureeTravail; }
    public String getRegimeTravail() { return regimeTravail; }
    public String getEmployeNom() { return employeNom; }
    public String getEmployePrenom() { return employePrenom; }
    public Long getEmployeId() { return employeId; }
}