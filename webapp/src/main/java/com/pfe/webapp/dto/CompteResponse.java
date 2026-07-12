package com.pfe.webapp.dto;

public class CompteResponse {
    private Long id;
    private String username;
    private String status;

    public CompteResponse(Long id, String username, String status) {
        this.id = id;
        this.username = username;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getStatus() { return status; }
}