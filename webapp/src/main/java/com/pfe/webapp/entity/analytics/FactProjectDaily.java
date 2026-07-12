// entity/analytics/FactProjectDaily.java
package com.pfe.webapp.entity.analytics;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "fact_project_daily")
public class FactProjectDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long missionId;

    @Column(nullable = false)
    private Long timeId;

    // Measures
    private Integer projectCount;
    private Integer activeProjects;
    private Integer completedProjects;
    private Integer delayedProjects;
    private Integer cancelledProjects;
    private Double totalBudget;
    private Double avgProgression;
    private Double avgBudgetUsage;

    // Metadata
    private LocalDate calculatedDate;

    // Constructors
    public FactProjectDaily() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public Long getTimeId() { return timeId; }
    public void setTimeId(Long timeId) { this.timeId = timeId; }

    public Integer getProjectCount() { return projectCount; }
    public void setProjectCount(Integer projectCount) { this.projectCount = projectCount; }

    public Integer getActiveProjects() { return activeProjects; }
    public void setActiveProjects(Integer activeProjects) { this.activeProjects = activeProjects; }

    public Integer getCompletedProjects() { return completedProjects; }
    public void setCompletedProjects(Integer completedProjects) { this.completedProjects = completedProjects; }

    public Integer getDelayedProjects() { return delayedProjects; }
    public void setDelayedProjects(Integer delayedProjects) { this.delayedProjects = delayedProjects; }

    public Integer getCancelledProjects() { return cancelledProjects; }
    public void setCancelledProjects(Integer cancelledProjects) { this.cancelledProjects = cancelledProjects; }

    public Double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(Double totalBudget) { this.totalBudget = totalBudget; }

    public Double getAvgProgression() { return avgProgression; }
    public void setAvgProgression(Double avgProgression) { this.avgProgression = avgProgression; }

    public Double getAvgBudgetUsage() { return avgBudgetUsage; }
    public void setAvgBudgetUsage(Double avgBudgetUsage) { this.avgBudgetUsage = avgBudgetUsage; }

    public LocalDate getCalculatedDate() { return calculatedDate; }
    public void setCalculatedDate(LocalDate calculatedDate) { this.calculatedDate = calculatedDate; }
}