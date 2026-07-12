package com.pfe.webapp.dto;

import com.pfe.webapp.entity.StatusCompte;

public class CompteResponseDTO {
    private Long id;
    private String username;
    private StatusCompte status;
    private String employeNom;
    private String employePrenom;
    private Long employeId;
    private String employeEmail;

    public CompteResponseDTO(Long id, String username, StatusCompte status,
                             String employeNom, String employePrenom,
                             Long employeId, String employeEmail) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.employeNom = employeNom;
        this.employePrenom = employePrenom;
        this.employeId = employeId;
        this.employeEmail = employeEmail;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public StatusCompte getStatus() { return status; }
    public String getEmployeNom() { return employeNom; }
    public String getEmployePrenom() { return employePrenom; }
    public Long getEmployeId() { return employeId; }
    public String getEmployeEmail() { return employeEmail; }
}