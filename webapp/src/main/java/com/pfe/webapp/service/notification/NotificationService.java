package com.pfe.webapp.service.notification;

import com.pfe.webapp.dto.notification.NotificationDTO;
import com.pfe.webapp.dto.notification.NotificationRequestDTO;
import com.pfe.webapp.entity.Compte;
import com.pfe.webapp.entity.Notification;
import com.pfe.webapp.entity.NotificationType;
import com.pfe.webapp.repository.CompteRepository;
import com.pfe.webapp.repository.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private CompteRepository compteRepository;

    // ========== CREATE NOTIFICATIONS ==========

    @Transactional
    public NotificationDTO createNotification(NotificationRequestDTO request) {
        Compte compte = compteRepository.findById(request.getCompteId())
                .orElseThrow(() -> new RuntimeException("Compte not found with id: " + request.getCompteId()));

        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setCompte(compte);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setLink(request.getLink());

        Notification saved = notificationRepository.save(notification);
        return convertToDTO(saved);
    }

    @Transactional
    public void createNotificationForCompte(Long compteId, String title, String message, NotificationType type) {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setCompteId(compteId);
        request.setTitle(title);
        request.setMessage(message);
        request.setType(type);
        createNotification(request);
    }

    @Transactional
    public void createNotificationForCompte(Long compteId, String title, String message, NotificationType type, String link) {
        NotificationRequestDTO request = new NotificationRequestDTO();
        request.setCompteId(compteId);
        request.setTitle(title);
        request.setMessage(message);
        request.setType(type);
        request.setLink(link);
        createNotification(request);
    }

    @Transactional
    public void createNotificationForUsers(List<Long> compteIds, String title, String message, NotificationType type) {
        for (Long compteId : compteIds) {
            createNotificationForCompte(compteId, title, message, type);
        }
    }

    @Transactional
    public void createNotificationForUsers(List<Long> compteIds, String title, String message, NotificationType type, String link) {
        for (Long compteId : compteIds) {
            createNotificationForCompte(compteId, title, message, type, link);
        }
    }

    @Transactional
    public void createNotificationForUsersWithRole(String roleName, String title, String message, NotificationType type) {
        List<Compte> comptes = compteRepository.findComptesByRole(roleName);
        for (Compte compte : comptes) {
            createNotificationForCompte(compte.getId(), title, message, type);
        }
    }

    @Transactional
    public void createNotificationForUsersWithRole(String roleName, String title, String message, NotificationType type, String link) {
        List<Compte> comptes = compteRepository.findComptesByRole(roleName);
        for (Compte compte : comptes) {
            createNotificationForCompte(compte.getId(), title, message, type, link);
        }
    }

    // ✅ NEW: Create notification with actor info
    @Transactional
    public void createNotificationForUsers(List<Long> compteIds, String title, String message,
                                           NotificationType type, String link, String actorName) {
        String fullMessage = message + " by " + actorName;
        for (Long compteId : compteIds) {
            createNotificationForCompte(compteId, title, fullMessage, type, link);
        }
    }

    // ========== GET NOTIFICATIONS ==========

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByCompte(Long compteId) {
        return notificationRepository.findByCompteId(compteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotificationsByCompte(Long compteId) {
        return notificationRepository.findUnreadByCompteId(compteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCountByCompte(Long compteId) {
        return notificationRepository.countUnreadByCompteId(compteId);
    }

    @Transactional(readOnly = true)
    public NotificationDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        return convertToDTO(notification);
    }

    // ========== UPDATE NOTIFICATIONS ==========

    @Transactional
    public NotificationDTO markAsRead(Long compteId, Long notificationId) {
        notificationRepository.markAsRead(compteId, notificationId);
        return getNotificationById(notificationId);
    }

    @Transactional
    public void markAllAsRead(Long compteId) {
        notificationRepository.markAllAsRead(compteId);
    }

    // ========== DELETE NOTIFICATIONS ==========

    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllRead(Long compteId) {
        notificationRepository.deleteAllRead(compteId);
    }

    // ========== CONVERTERS ==========

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setLink(notification.getLink());
        if (notification.getCompte() != null) {
            dto.setCompteId(notification.getCompte().getId());
            dto.setCompteUsername(notification.getCompte().getUsername());
        }
        return dto;
    }
}