package com.pfe.webapp.repository.av;

import com.pfe.webapp.entity.EtatAvancement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtatAvancementRepository extends JpaRepository<EtatAvancement, Long> {

    List<EtatAvancement> findByProjectId(Long projectId);

    List<EtatAvancement> findByActiveId(Long activeId);

    Optional<EtatAvancement> findByProjectIdAndActiveId(Long projectId, Long activeId);

    @Query("SELECT e FROM EtatAvancement e LEFT JOIN FETCH e.avancements WHERE e.project.id = :projectId")
    List<EtatAvancement> findByProjectIdWithAvancements(@Param("projectId") Long projectId);

    @Query("SELECT e FROM EtatAvancement e LEFT JOIN FETCH e.avancements WHERE e.id = :id")
    Optional<EtatAvancement> findByIdWithAvancements(@Param("id") Long id);



    // ✅ NEW: Find all etatAvancements for a project where active is NOT NULL (activity statuses)
    @Query("SELECT e FROM EtatAvancement e WHERE e.project.id = :projectId AND e.active IS NOT NULL")
    List<EtatAvancement> findByProjectIdAndActiveIsNotNull(@Param("projectId") Long projectId);

    // ✅ NEW: Find all etatAvancements for a project where active is NULL (project status)
    @Query("SELECT e FROM EtatAvancement e WHERE e.project.id = :projectId AND e.active IS NULL")
    List<EtatAvancement> findByProjectIdAndActiveIsNull(@Param("projectId") Long projectId);


    @Query("SELECT e FROM EtatAvancement e WHERE e.active.id = :activeId AND e.project.mission.id = :missionId")
    Optional<EtatAvancement> findByActiveIdAndMissionId(@Param("activeId") Long activeId, @Param("missionId") Long missionId);
}