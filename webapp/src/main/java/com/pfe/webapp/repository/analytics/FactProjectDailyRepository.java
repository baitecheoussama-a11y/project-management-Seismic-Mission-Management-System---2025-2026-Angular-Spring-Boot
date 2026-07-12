// repository/analytics/FactProjectDailyRepository.java
package com.pfe.webapp.repository.analytics;

import com.pfe.webapp.entity.analytics.FactProjectDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FactProjectDailyRepository extends JpaRepository<FactProjectDaily, Long> {

    @Query("SELECT SUM(f.projectCount) FROM FactProjectDaily f")
    Integer sumTotalProjects();

    @Query("SELECT SUM(f.activeProjects) FROM FactProjectDaily f")
    Integer sumActiveProjects();

    @Query("SELECT SUM(f.completedProjects) FROM FactProjectDaily f")
    Integer sumCompletedProjects();

    @Query("SELECT SUM(f.delayedProjects) FROM FactProjectDaily f")
    Integer sumDelayedProjects();

    @Query("SELECT AVG(f.avgProgression) FROM FactProjectDaily f")
    Double avgProgression();

    @Query("SELECT f FROM FactProjectDaily f WHERE f.calculatedDate = :date")
    List<FactProjectDaily> findByDate(@Param("date") LocalDate date);

    @Query("SELECT f FROM FactProjectDaily f WHERE f.missionId = :missionId " +
            "AND f.calculatedDate BETWEEN :startDate AND :endDate")
    List<FactProjectDaily> findByMissionAndDateRange(@Param("missionId") Long missionId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    // repository/analytics/FactProjectDailyRepository.java - أضف هذه الطرق

    @Query("SELECT f FROM FactProjectDaily f WHERE f.calculatedDate = :date AND f.missionId = :missionId")
    FactProjectDaily findByDateAndMission(@Param("date") LocalDate date, @Param("missionId") Long missionId);
}