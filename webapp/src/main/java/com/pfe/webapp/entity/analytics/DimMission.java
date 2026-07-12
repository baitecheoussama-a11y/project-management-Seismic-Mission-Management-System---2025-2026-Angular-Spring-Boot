// entity/analytics/DimMission.java
package com.pfe.webapp.entity.analytics;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_mission")
public class DimMission {

    @Id
    private Long id; // Same as Mission.id

    @Column(nullable = false)
    private String codeMission;

    private String methodologie;

    private String description;

    // Constructors
    public DimMission() {}

    public DimMission(Long id, String codeMission, String methodologie, String description) {
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

    public String getMethodologie() { return methodologie; }
    public void setMethodologie(String methodologie) { this.methodologie = methodologie; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}