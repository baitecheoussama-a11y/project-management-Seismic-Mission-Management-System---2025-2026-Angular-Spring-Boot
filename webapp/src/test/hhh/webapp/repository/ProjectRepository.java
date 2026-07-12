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

    // Get current active project (not completed or cancelled)
    @Query("SELECT p FROM Project p " +
            "LEFT JOIN p.etatAvancements e " +
            "WHERE p.mission.id = :missionId " +
            "AND p.annule = false " +
            "AND (e.active IS NULL) " +
            "AND e.status NOT IN ('TERMINI', 'ANNULE') " +
            "ORDER BY p.id DESC")
    Optional<Project> findCurrentActiveProjectByMissionId(@Param("missionId") Long missionId);

    // Alternative query using JPQL with subquery for latest status
    @Query("SELECT p FROM Project p " +
            "WHERE p.mission.id = :missionId " +
            "AND p.annule = false " +
            "AND EXISTS (SELECT 1 FROM EtatAvancement e " +
            "            WHERE e.project = p " +
            "            AND e.active IS NULL " +
            "            AND e.status NOT IN ('TERMINI', 'ANNULE'))")
    Optional<Project> findCurrentActiveProjectByMissionIdV2(@Param("missionId") Long missionId);

    // Check if mission has an active project (not completed or cancelled)
    @Query("SELECT COUNT(p) > 0 FROM Project p " +
            "LEFT JOIN p.etatAvancements e " +
            "WHERE p.mission.id = :missionId " +
            "AND p.annule = false " +
            "AND e.active IS NULL " +
            "AND e.status NOT IN ('TERMINI', 'ANNULE')")
    boolean existsActiveProjectByMissionId(@Param("missionId") Long missionId);

    // Get all active projects (not completed or cancelled)
    @Query("SELECT p FROM Project p " +
            "LEFT JOIN p.etatAvancements e " +
            "WHERE p.annule = false " +
            "AND e.active IS NULL " +
            "AND e.status NOT IN ('TERMINI', 'ANNULE')")
    List<Project> findAllActiveProjects();

    // ✅ NEW: Get ALL projects for a mission (including cancelled/annulled)
    @Query("SELECT p FROM Project p WHERE p.mission.id = :missionId ORDER BY p.id DESC")
    List<Project> findAllProjectsByMissionId(@Param("missionId") Long missionId);
}