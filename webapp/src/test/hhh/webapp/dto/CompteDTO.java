package com.pfe.webapp.dto;

import com.pfe.webapp.entity.StatusCompte;

public class CompteDTO {
    private String username;
    private StatusCompte status;
    private Long employeId;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public StatusCompte getStatus() { return status; }
    public void setStatus(StatusCompte status) { this.status = status; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }
}