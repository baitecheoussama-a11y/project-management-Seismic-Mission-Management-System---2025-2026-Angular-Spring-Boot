package com.pfe.webapp.dto;

import java.util.List;

public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String status;
    private List<String> roles;  // ✅ أضف هذا

    public LoginResponse(String token, Long id, String username, String status, List<String> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.status = status;
        this.roles = roles;
    }

    // Getters
    public String getToken() { return token; }
    public String getType() { return type; }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getStatus() { return status; }
    public List<String> getRoles() { return roles; }  // ✅ أضف هذا
}