package com.pfe.webapp.dto.notification;

import com.pfe.webapp.entity.NotificationType;

public class NotificationRequestDTO {
    private Long compteId;
    private String title;
    private String message;
    private NotificationType type;
    private String link;

    // Getters and Setters
    public Long getCompteId() { return compteId; }
    public void setCompteId(Long compteId) { this.compteId = compteId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}