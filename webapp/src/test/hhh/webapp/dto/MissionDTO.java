// dto/MissionDTO.java
package com.pfe.webapp.dto;

import com.pfe.webapp.entity.TypeMission;
import java.time.LocalDate;
import java.util.List;

public class MissionDTO {
    private Long id;
    private String codeMission;
    private TypeMission methodologie;
    private String description;

    private int totalConsumptions;
    private double totalConsumptionCost;

    public MissionDTO() {}

    public MissionDTO(Long id, String codeMission, TypeMission methodologie, String description) {
        this.id = id;
        this.codeMission = codeMission;
        this.methodologie = methodologie;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodeMission() { return codeMission; }
    public void setCodeMission(String codeMission) { this.codeMission = codeMission; }

    public TypeMission getMethodologie() { return methodologie; }
    public void setMethodologie(TypeMission methodologie) { this.methodologie = methodologie; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }



    public int getTotalConsumptions() { return totalConsumptions; }
    public void setTotalConsumptions(int totalConsumptions) { this.totalConsumptions = totalConsumptions; }

    public double getTotalConsumptionCost() { return totalConsumptionCost; }
    public void setTotalConsumptionCost(double totalConsumptionCost) { this.totalConsumptionCost = totalConsumptionCost; }
}