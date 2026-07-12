// dto/pointage/PointageRequestDTO.java
package com.pfe.webapp.dto.pointage;

import java.time.LocalDate;

public class PointageRequestDTO {
    private Long employeId;
    private LocalDate datePointage;
    private String status;
    private String motifAbsent;
    private String remarque;

    // Getters and Setters
    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public LocalDate getDatePointage() { return datePointage; }
    public void setDatePointage(LocalDate datePointage) { this.datePointage = datePointage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMotifAbsent() { return motifAbsent; }
    public void setMotifAbsent(String motifAbsent) { this.motifAbsent = motifAbsent; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }
}