package com.pfe.webapp.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Compte extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private StatusCompte status;

    @OneToOne
    @JoinColumn(name = "employe_id", unique = true)
    private Employe employe;

    @OneToMany(mappedBy = "compte")
    private List<AffectationRole> roles;

    // ✅ NEW: One-to-Many relationship with Notifications
    @OneToMany(mappedBy = "compte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public StatusCompte getStatus() { return status; }
    public void setStatus(StatusCompte status) { this.status = status; }

    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }

    public List<AffectationRole> getRoles() { return roles; }
    public void setRoles(List<AffectationRole> roles) { this.roles = roles; }

    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }

    // Helper methods
    public void addNotification(Notification notification) {
        notifications.add(notification);
        notification.setCompte(this);
    }

    public void removeNotification(Notification notification) {
        notifications.remove(notification);
        notification.setCompte(null);
    }
}