// dto/analytics/ProjectStatusDTO.java
package com.pfe.webapp.dto.analytics;

public class ProjectStatusDTO {
    private Integer total;
    private Integer active;
    private Integer completed;
    private Integer delayed;
    private Integer cancelled;
    private Double avgProgression;

    // Constructor
    public ProjectStatusDTO() {}

    // Getters and Setters
    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Integer getDelayed() {
        return delayed;
    }

    public void setDelayed(Integer delayed) {
        this.delayed = delayed;
    }

    public Integer getCancelled() {
        return cancelled;
    }

    public void setCancelled(Integer cancelled) {
        this.cancelled = cancelled;
    }

    public Double getAvgProgression() {
        return avgProgression;
    }

    public void setAvgProgression(Double avgProgression) {
        this.avgProgression = avgProgression;
    }
}