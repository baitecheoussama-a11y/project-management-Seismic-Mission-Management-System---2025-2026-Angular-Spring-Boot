package com.pfe.webapp.controller.notification;

import com.pfe.webapp.dto.notification.NotificationDTO;
import com.pfe.webapp.dto.notification.NotificationRequestDTO;
import com.pfe.webapp.dto.notification.UnreadCountDTO;
import com.pfe.webapp.entity.Compte;
import com.pfe.webapp.repository.CompteRepository;
import com.pfe.webapp.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CompteRepository compteRepository; // ✅ ADD THIS

    // ========== GET NOTIFICATIONS ==========

    @GetMapping("/my")
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        Long compteId = getCompteIdFromUserDetails(userDetails);
        if (compteId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(notificationService.getNotificationsByCompte(compteId));
    }

    @GetMapping("/my/unread")
    public ResponseEntity<List<NotificationDTO>> getMyUnreadNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        Long compteId = getCompteIdFromUserDetails(userDetails);
        if (compteId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByCompte(compteId));
    }

    @GetMapping("/my/unread/count")
    public ResponseEntity<UnreadCountDTO> getMyUnreadCount(@AuthenticationPrincipal UserDetails userDetails) {
        Long compteId = getCompteIdFromUserDetails(userDetails);
        if (compteId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        long count = notificationService.getUnreadCountByCompte(compteId);
        return ResponseEntity.ok(new UnreadCountDTO(count));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    // ========== CREATE NOTIFICATIONS (ADMIN ONLY) ==========

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationRequestDTO request) {
        NotificationDTO created = notificationService.createNotification(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ========== UPDATE NOTIFICATIONS ==========

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long compteId = getCompteIdFromUserDetails(userDetails);
        if (compteId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(notificationService.markAsRead(compteId, id));
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        Long compteId = getCompteIdFromUserDetails(userDetails);
        if (compteId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        notificationService.markAllAsRead(compteId);
        return ResponseEntity.ok().build();
    }

    // ========== DELETE NOTIFICATIONS ==========

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/read")
    public ResponseEntity<Void> deleteAllRead(@AuthenticationPrincipal UserDetails userDetails) {
        Long compteId = getCompteIdFromUserDetails(userDetails);
        if (compteId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        notificationService.deleteAllRead(compteId);
        return ResponseEntity.noContent().build();
    }

    // ========== HELPER METHODS ==========

    private Long getCompteIdFromUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }

        try {
            String username = userDetails.getUsername();
            Compte compte = compteRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Compte not found for username: " + username));
            return compte.getId();
        } catch (Exception e) {
            return null;
        }
    }
}