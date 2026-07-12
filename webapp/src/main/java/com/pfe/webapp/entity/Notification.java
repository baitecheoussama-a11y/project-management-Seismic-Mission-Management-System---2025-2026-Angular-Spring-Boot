package com.pfe.webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private boolean read;
    private LocalDateTime createdAt;
    private String link; // Optional: link to related entity

    @ManyToOne
    @JoinColumn(name = "compte_id")
    private Compte compte;

    // ========== Constructors ==========
    public Notification() {}

    public Notification(String title, String message, NotificationType type, Compte compte) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.compte = compte;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    // ========== Getters and Setters ==========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public Compte getCompte() { return compte; }
    public void setCompte(Compte compte) { this.compte = compte; }
}