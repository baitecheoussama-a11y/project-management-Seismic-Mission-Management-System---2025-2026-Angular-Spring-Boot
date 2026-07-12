// repository/analytics/DimTimeRepository.java
package com.pfe.webapp.repository.analytics;

import com.pfe.webapp.entity.analytics.DimTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DimTimeRepository extends JpaRepository<DimTime, Long> {

    Optional<DimTime> findByDate(LocalDate date);

    @Query("SELECT t FROM DimTime t WHERE t.year = :year AND t.month = :month")
    List<DimTime> findByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("SELECT t FROM DimTime t WHERE t.year = :year")
    List<DimTime> findByYear(@Param("year") Integer year);
}