// repository/analytics/FactConsommationRepository.java
package com.pfe.webapp.repository.analytics;

import com.pfe.webapp.entity.analytics.FactConsommation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FactConsommationRepository extends JpaRepository<FactConsommation, Long> {

    @Query("SELECT SUM(f.totalCost) FROM FactConsommation f")
    Double sumTotalCost();

    @Query("SELECT f.missionId, SUM(f.totalCost) FROM FactConsommation f " +
            "WHERE f.calculatedDate = :date GROUP BY f.missionId")
    List<Object[]> findCostByMission(@Param("date") LocalDate date);

    @Query("SELECT f FROM FactConsommation f WHERE f.missionId = :missionId " +
            "AND f.calculatedDate BETWEEN :startDate AND :endDate")
    List<FactConsommation> findByMissionAndDateRange(@Param("missionId") Long missionId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    @Query("SELECT f.resourceId, SUM(f.totalCost) FROM FactConsommation f " +
            "WHERE f.calculatedDate = :date GROUP BY f.resourceId ORDER BY SUM(f.totalCost) DESC")
    List<Object[]> findTopResourcesByCost(@Param("date") LocalDate date);

    // repository/analytics/FactConsommationRepository.java - أضف هذه الطرق

    @Query("SELECT SUM(f.totalCost) FROM FactConsommation f WHERE f.calculatedDate = :date")
    Double sumTotalCostByDate(@Param("date") LocalDate date);

    @Query("SELECT SUM(f.totalCost) FROM FactConsommation f WHERE f.missionId = :missionId AND f.calculatedDate = :date")
    Double sumTotalCostByMissionAndDate(@Param("missionId") Long missionId, @Param("date") LocalDate date);

    @Query("SELECT f.resourceId, SUM(f.totalCost) FROM FactConsommation f " +
            "WHERE f.calculatedDate = :date GROUP BY f.resourceId ORDER BY SUM(f.totalCost) DESC")
    List<Object[]> findTopResourcesByCost(@Param("date") LocalDate date, @Param("limit") int limit);
}