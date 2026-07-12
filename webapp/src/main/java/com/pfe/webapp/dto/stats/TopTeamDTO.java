package com.pfe.webapp.dto.stats;

public class TopTeamDTO {
    private Integer rank;
    private String teamName;
    private Double productionValue;

    // ✅ Constructor for query (without rank - will be set manually)
    public TopTeamDTO(String teamName, Double productionValue) {
        this.teamName = teamName;
        this.productionValue = productionValue;
        this.rank = 0;
    }

    // ✅ Constructor with rank
    public TopTeamDTO(Integer rank, String teamName, Double productionValue) {
        this.rank = rank;
        this.teamName = teamName;
        this.productionValue = productionValue;
    }

    // Default constructor
    public TopTeamDTO() {}

    // Getters and Setters
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Double getProductionValue() { return productionValue; }
    public void setProductionValue(Double productionValue) { this.productionValue = productionValue; }
}