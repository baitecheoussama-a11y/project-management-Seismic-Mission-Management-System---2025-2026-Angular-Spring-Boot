package com.pfe.webapp.dto.team;

public class StatActivitesDTO {
    private int totalActivites;
    private int totalRapports;
    private int totalRendements;
    private double moyenneRendement;
    private double totalHeuresTravaillees;

    // Getters and Setters
    public int getTotalActivites() { return totalActivites; }
    public void setTotalActivites(int totalActivites) { this.totalActivites = totalActivites; }

    public int getTotalRapports() { return totalRapports; }
    public void setTotalRapports(int totalRapports) { this.totalRapports = totalRapports; }

    public int getTotalRendements() { return totalRendements; }
    public void setTotalRendements(int totalRendements) { this.totalRendements = totalRendements; }

    public double getMoyenneRendement() { return moyenneRendement; }
    public void setMoyenneRendement(double moyenneRendement) { this.moyenneRendement = moyenneRendement; }

    public double getTotalHeuresTravaillees() { return totalHeuresTravaillees; }
    public void setTotalHeuresTravaillees(double totalHeuresTravaillees) { this.totalHeuresTravaillees = totalHeuresTravaillees; }
}