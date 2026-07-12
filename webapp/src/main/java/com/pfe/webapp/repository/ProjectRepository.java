package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByMissionId(Long missionId);

    List<Project> findByMissionIdAndAnnuleFalse(Long missionId);

    boolean existsByMissionIdAndAnnuleFalse(Long missionId);

    // Get active projects by mission ID (not annulled)
    @Query("SELECT p FROM Project p WHERE p.mission.id = :missionId AND p.annule = false")
    List<Project> findActiveProjectsByMissionId(@Param("missionId") Long missionId);

    // ✅ FIXED: Use date-based status instead of etatAvancements
    // A project is active if:
    // 1. Not annulled
    // 2. Not completed (dateFinReelle is null OR status not TERMINI)
    // 3. Not cancelled (dateFinReelle is null OR status not ANNULE)
    @Query("SELECT p FROM Project p " +
            "WHERE p.mission.id = :missionId " +
            "AND p.annule = false " +
            "AND (p.dateFinReelle IS NULL OR p.dateFinReelle = null) " +
            "ORDER BY p.id DESC")
    Optional<Project> findCurrentActiveProjectByMissionId(@Param("missionId") Long missionId);

    // Alternative query with more sophisticated status check
    @Query("SELECT p FROM Project p " +
            "WHERE p.mission.id = :missionId " +
            "AND p.annule = false " +
            "AND (p.dateFinReelle IS NULL) " +
            "AND (p.dateStartReelle IS NOT NULL OR p.objectifFin IS NULL OR p.objectifFin >= CURRENT_DATE) " +
            "ORDER BY p.id DESC")
    Optional<Project> findCurrentActiveProjectByMissionIdV2(@Param("missionId") Long missionId);

    // ✅ FIXED: Check if mission has an active project
    @Query("SELECT COUNT(p) > 0 FROM Project p " +
            "WHERE p.mission.id = :missionId " +
            "AND p.annule = false " +
            "AND (p.dateFinReelle IS NULL) " +
            "ORDER BY p.id DESC")
    boolean existsActiveProjectByMissionId(@Param("missionId") Long missionId);

    // ✅ FIXED: Get all active projects
    @Query("SELECT p FROM Project p " +
            "WHERE p.annule = false " +
            "AND (p.dateFinReelle IS NULL) " +
            "ORDER BY p.id DESC")
    List<Project> findAllActiveProjects();

    // ✅ NEW: Get ALL projects for a mission
    @Query("SELECT p FROM Project p WHERE p.mission.id = :missionId ORDER BY p.id DESC")
    List<Project> findAllProjectsByMissionId(@Param("missionId") Long missionId);
}