package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Pointage;
import com.pfe.webapp.entity.StatusPointage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointageRepository extends JpaRepository<Pointage, Long> {

    @Query("SELECT p FROM Pointage p WHERE p.employe.id = :employeId ORDER BY p.datePointage DESC")
    List<Pointage> findByEmployeId(@Param("employeId") Long employeId);

    @Query("SELECT p FROM Pointage p WHERE p.employe.id = :employeId AND p.status = :status ORDER BY p.datePointage DESC")
    List<Pointage> findByEmployeIdAndStatus(@Param("employeId") Long employeId,
                                            @Param("status") StatusPointage status);

    @Query("SELECT p FROM Pointage p WHERE p.employe.id = :employeId AND p.datePointage BETWEEN :startDate AND :endDate ORDER BY p.datePointage DESC")
    List<Pointage> findByEmployeIdAndDateRange(@Param("employeId") Long employeId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Pointage p WHERE p.datePointage = :date")
    List<Pointage> findByDate(@Param("date") LocalDate date);

    @Query("SELECT p FROM Pointage p WHERE p.datePointage = :date AND p.status = :status")
    List<Pointage> findByDateAndStatus(@Param("date") LocalDate date,
                                       @Param("status") StatusPointage status);

    @Query("SELECT p FROM Pointage p WHERE p.employe.id = :employeId AND p.datePointage = :date")
    Optional<Pointage> findByEmployeIdAndDate(@Param("employeId") Long employeId,
                                              @Param("date") LocalDate date);

    @Query("SELECT p FROM Pointage p WHERE p.datePointage BETWEEN :startDate AND :endDate ORDER BY p.datePointage DESC")
    List<Pointage> findByDateRange(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(p) FROM Pointage p WHERE p.status = :status")
    long countByStatus(@Param("status") StatusPointage status);

    @Query("SELECT COUNT(p) FROM Pointage p WHERE p.employe.id = :employeId AND p.status = :status")
    long countByEmployeIdAndStatus(@Param("employeId") Long employeId,
                                   @Param("status") StatusPointage status);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pointage p WHERE p.employe.id = :employeId AND p.datePointage = :date")
    boolean existsByEmployeIdAndDate(@Param("employeId") Long employeId,
                                     @Param("date") LocalDate date);

    @Query("SELECT p FROM Pointage p WHERE p.datePointage = CURRENT_DATE")
    List<Pointage> findTodaysPointages();

    @Query("SELECT p FROM Pointage p WHERE p.datePointage = CURRENT_DATE AND p.status = :status")
    List<Pointage> findTodaysPointagesByStatus(@Param("status") StatusPointage status);
}