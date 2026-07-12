// dto/pointage/PointageResponseDTO.java
package com.pfe.webapp.dto.pointage;

import com.pfe.webapp.entity.StatusPointage;
import java.time.LocalDate;

public class PointageResponseDTO {
    private Long id;
    private LocalDate datePointage;
    private StatusPointage status;
    private String motifAbsent;
    private String remarque;
    private Long employeId;
    private String employeNom;
    private String employePrenom;
    private String statusLabel;
    private String statusColor;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDatePointage() { return datePointage; }
    public void setDatePointage(LocalDate datePointage) { this.datePointage = datePointage; }

    public StatusPointage getStatus() { return status; }
    public void setStatus(StatusPointage status) { this.status = status; }

    public String getMotifAbsent() { return motifAbsent; }
    public void setMotifAbsent(String motifAbsent) { this.motifAbsent = motifAbsent; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public String getEmployeNom() { return employeNom; }
    public void setEmployeNom(String employeNom) { this.employeNom = employeNom; }

    public String getEmployePrenom() { return employePrenom; }
    public void setEmployePrenom(String employePrenom) { this.employePrenom = employePrenom; }

    public String getStatusLabel() { return statusLabel; }
    public void setStatusLabel(String statusLabel) { this.statusLabel = statusLabel; }

    public String getStatusColor() { return statusColor; }
    public void setStatusColor(String statusColor) { this.statusColor = statusColor; }
}