// dto/team/EquipeDetailDTO.java
package com.pfe.webapp.dto.team;

import java.util.List;

public class EquipeDetailDTO {
    private Long id;
    private String nom;
    private String type;
    private List<EmployeSimpleDTO> membres;
    private List<ActiveDTO> activites;
    private List<RapportDTO> rapports;
    private List<RendementDTO> rendements;
    private StatActivitesDTO statistiques;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<EmployeSimpleDTO> getMembres() { return membres; }
    public void setMembres(List<EmployeSimpleDTO> membres) { this.membres = membres; }

    public List<ActiveDTO> getActivites() { return activites; }
    public void setActivites(List<ActiveDTO> activites) { this.activites = activites; }

    public List<RapportDTO> getRapports() { return rapports; }
    public void setRapports(List<RapportDTO> rapports) { this.rapports = rapports; }

    public List<RendementDTO> getRendements() { return rendements; }
    public void setRendements(List<RendementDTO> rendements) { this.rendements = rendements; }

    public StatActivitesDTO getStatistiques() { return statistiques; }
    public void setStatistiques(StatActivitesDTO statistiques) { this.statistiques = statistiques; }
}