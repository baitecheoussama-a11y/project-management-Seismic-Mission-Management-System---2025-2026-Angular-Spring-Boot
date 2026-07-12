package com.pfe.webapp.repository.project;

import com.pfe.webapp.entity.Active;
import com.pfe.webapp.entity.AffectationEquipe;
import com.pfe.webapp.entity.Rapport;
import com.pfe.webapp.entity.Rendement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectProgressRepository extends JpaRepository<Rapport, Long> {

    // Get all reports for a project
    @Query("SELECT r FROM Rapport r WHERE r.project.id = :projectId")
    List<Rapport> findRapportsByProjectId(@Param("projectId") Long projectId);

    // Get all Rendements through project
    @Query("SELECT r FROM Rendement r WHERE r.rapport.project.id = :projectId")
    List<Rendement> findRendementsByProjectId(@Param("projectId") Long projectId);

    // Get actives through AffectationEquipe
    @Query("SELECT DISTINCT ae.active FROM AffectationEquipe ae WHERE ae.project.id = :projectId AND ae.active IS NOT NULL")
    List<Active> findActivesByProjectId(@Param("projectId") Long projectId);

    // ✅ NEW: Get AffectationEquipe for multiple active IDs and a specific project
    @Query("SELECT ae FROM AffectationEquipe ae WHERE ae.active.id IN :activeIds AND ae.project.id = :projectId")
    List<AffectationEquipe> findAffectationEquipesByActiveIdsAndProject(
            @Param("activeIds") List<Long> activeIds,
            @Param("projectId") Long projectId);

    // ✅ NEW: Get AffectationEquipe for a specific active and project
    @Query("SELECT ae FROM AffectationEquipe ae WHERE ae.active.id = :activeId AND ae.project.id = :projectId")
    List<AffectationEquipe> findAffectationEquipesByActiveIdAndProject(
            @Param("activeId") Long activeId,
            @Param("projectId") Long projectId);
}