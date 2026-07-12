package com.pfe.webapp.repository.event;

import com.pfe.webapp.entity.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    // جلب الأحداث حسب المهمة
    List<Evenement> findByMissionId(Long missionId);

    // جلب الأحداث حسب نوع الحدث
    List<Evenement> findByTypeEvenementId(Long typeEvenementId);

    // جلب الأحداث حسب التاريخ
    List<Evenement> findByDate(LocalDate date);

    // جلب الأحداث بين تاريخين
    List<Evenement> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // جلب الأحداث القادمة
    @Query("SELECT e FROM Evenement e WHERE e.date >= :today ORDER BY e.date ASC")
    List<Evenement> findUpcomingEvents(@Param("today") LocalDate today);

    // جلب الأحداث السابقة
    @Query("SELECT e FROM Evenement e WHERE e.date < :today ORDER BY e.date DESC")
    List<Evenement> findPastEvents(@Param("today") LocalDate today);

    // جلب أحداث اليوم
    @Query("SELECT e FROM Evenement e WHERE e.date = :today")
    List<Evenement> findTodaysEvents(@Param("today") LocalDate today);

    // البحث عن الأحداث
    @Query("SELECT e FROM Evenement e WHERE " +
            "LOWER(e.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Evenement> searchByKeyword(@Param("keyword") String keyword);

    // جلب الأحداث القادمة لمهمة محددة
    @Query("SELECT e FROM Evenement e WHERE e.mission.id = :missionId AND e.date >= :today")
    List<Evenement> findUpcomingEventsByMission(@Param("missionId") Long missionId,
                                                @Param("today") LocalDate today);

    // عدد الأحداث بين تاريخين
    long countByDateBetween(LocalDate startDate, LocalDate endDate);
}