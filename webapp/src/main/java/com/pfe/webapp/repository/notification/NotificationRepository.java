package com.pfe.webapp.repository.notification;

import com.pfe.webapp.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.compte.id = :compteId ORDER BY n.createdAt DESC")
    List<Notification> findByCompteId(@Param("compteId") Long compteId);

    @Query("SELECT n FROM Notification n WHERE n.compte.id = :compteId AND n.read = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByCompteId(@Param("compteId") Long compteId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.compte.id = :compteId AND n.read = false")
    long countUnreadByCompteId(@Param("compteId") Long compteId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.compte.id = :compteId AND n.id = :notificationId")
    void markAsRead(@Param("compteId") Long compteId, @Param("notificationId") Long notificationId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.compte.id = :compteId")
    void markAllAsRead(@Param("compteId") Long compteId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.compte.id = :compteId AND n.read = true")
    void deleteAllRead(@Param("compteId") Long compteId);
}