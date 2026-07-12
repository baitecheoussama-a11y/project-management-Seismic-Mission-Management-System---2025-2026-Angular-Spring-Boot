package com.pfe.webapp.repository.project;

import com.pfe.webapp.entity.Active;
import com.pfe.webapp.entity.EtatAvancement;
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

    // Get all reports for a project (Rapport -> Project)
    @Query("SELECT r FROM Rapport r WHERE r.project.id = :projectId")
    List<Rapport> findRapportsByProjectId(@Param("projectId") Long projectId);

    // Get all Rendements through project (Rendement -> Rapport -> Project)
    @Query("SELECT r FROM Rendement r WHERE r.rapport.project.id = :projectId")
    List<Rendement> findRendementsByProjectId(@Param("projectId") Long projectId);

    // Get actives from rendements (Rendement -> AffectationEquipe -> Active)
    @Query("SELECT DISTINCT r.affectationEquipe.active FROM Rendement r " +
            "WHERE r.rapport.project.id = :projectId " +
            "AND r.affectationEquipe.active IS NOT NULL")
    List<Active> findActivesByProjectId(@Param("projectId") Long projectId);

    // Get EtatAvancement for a specific active
    @Query("SELECT ea FROM EtatAvancement ea WHERE ea.active.id = :activeId")
    Optional<EtatAvancement> findEtatAvancementByActiveId(@Param("activeId") Long activeId);

    // Get all EtatAvancements for multiple actives
    @Query("SELECT ea FROM EtatAvancement ea WHERE ea.active.id IN :activeIds")
    List<EtatAvancement> findEtatAvancementsByActiveIds(@Param("activeIds") List<Long> activeIds);

    // Optional: Get active by ID directly
    @Query("SELECT a FROM Active a WHERE a.id = :activeId")
    Optional<Active> findActiveById(@Param("activeId") Long activeId);
}