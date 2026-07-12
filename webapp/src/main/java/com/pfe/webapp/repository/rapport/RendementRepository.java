package com.pfe.webapp.repository.rapport;

import com.pfe.webapp.dto.stats.*;
import com.pfe.webapp.entity.Rendement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RendementRepository extends JpaRepository<Rendement, Long> {

    List<Rendement> findByRapportId(Long rapportId);

    List<Rendement> findByAffectationEquipeId(Long affectationEquipeId);

    @Query("SELECT AVG(r.valeurRendement) FROM Rendement r WHERE r.affectationEquipe.id = :affectationEquipeId")
    Double getAverageRendementByAffectationEquipe(@Param("affectationEquipeId") Long affectationEquipeId);

    @Query("SELECT SUM(r.valeurRendement) FROM Rendement r WHERE r.rapport.id = :rapportId")
    Double getTotalRendementByRapport(@Param("rapportId") Long rapportId);

    @Query("SELECT r FROM Rendement r WHERE r.affectationEquipe.equipe.id = :equipeId ORDER BY r.date DESC")
    List<Rendement> findRendementsByEquipeId(@Param("equipeId") Long equipeId);

    @Query("SELECT AVG(r.valeurRendement) FROM Rendement r WHERE r.affectationEquipe.equipe.id = :equipeId")
    Double getAverageRendementByEquipeId(@Param("equipeId") Long equipeId);

    @Query("SELECT r FROM Rendement r WHERE r.rapport.id = :rapportId AND r.affectationEquipe.equipe.id = :equipeId")
    List<Rendement> findByRapportIdAndEquipeId(@Param("rapportId") Long rapportId, @Param("equipeId") Long equipeId);

    @Query("SELECT r FROM Rendement r WHERE r.rapport.id = :rapportId AND r.affectationEquipe.mission.id = :missionId")
    List<Rendement> findByRapportIdAndMissionId(@Param("rapportId") Long rapportId, @Param("missionId") Long missionId);

    @Query("SELECT r FROM Rendement r WHERE r.affectationEquipe.equipe.id = :equipeId AND r.affectationEquipe.mission.id = :missionId ORDER BY r.date DESC")
    List<Rendement> findByEquipeIdAndMissionId(@Param("equipeId") Long equipeId, @Param("missionId") Long missionId);

    // ========== KPI QUERIES ==========

    @Query("SELECT COUNT(r) FROM Rendement r")
    Long countTotalProductionRecords();

    @Query("SELECT SUM(r.valeurRendement) / SUM(r.dureeHeures) FROM Rendement r")
    Double getAverageProductivity();

    @Query("SELECT COUNT(DISTINCT r.affectationEquipe.equipe.id) FROM Rendement r")
    Long countActiveTeams();

    // ✅ FIXED: Average Activity Duration using TIMESTAMPDIFF
    @Query("SELECT AVG(TIMESTAMPDIFF(DAY, r.affectationEquipe.dateStartReelle, r.affectationEquipe.dateFinReelle)) " +
            "FROM Rendement r WHERE r.affectationEquipe.dateStartReelle IS NOT NULL AND r.affectationEquipe.dateFinReelle IS NOT NULL")
    Double getAverageActivityDuration();

    // ========== CHART QUERIES ==========

    // ✅ FIXED: Production By Team - removed ROW_NUMBER
    @Query("SELECT new com.pfe.webapp.dto.stats.ProductionByTeamDTO(e.nom, SUM(r.valeurRendement)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.equipe e " +
            "GROUP BY e.nom " +
            "ORDER BY SUM(r.valeurRendement) DESC")
    List<ProductionByTeamDTO> getProductionByTeam();

    @Query("SELECT new com.pfe.webapp.dto.stats.ProductionByTeamDTO(e.nom, SUM(r.valeurRendement)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.equipe e " +
            "WHERE r.date BETWEEN :startDate AND :endDate " +
            "GROUP BY e.nom " +
            "ORDER BY SUM(r.valeurRendement) DESC")
    List<ProductionByTeamDTO> getProductionByTeam(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    // ✅ FIXED: Production By Activity - removed ROW_NUMBER
    @Query("SELECT new com.pfe.webapp.dto.stats.ProductionByActivityDTO(a.codeActive, a.objectif, SUM(r.valeurRendement)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.active a " +
            "WHERE a IS NOT NULL " +
            "GROUP BY a.codeActive, a.objectif " +
            "ORDER BY SUM(r.valeurRendement) DESC")
    List<ProductionByActivityDTO> getProductionByActivity();

    @Query("SELECT new com.pfe.webapp.dto.stats.ProductionByActivityDTO(a.codeActive, a.objectif, SUM(r.valeurRendement)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.active a " +
            "WHERE a IS NOT NULL AND r.date BETWEEN :startDate AND :endDate " +
            "GROUP BY a.codeActive, a.objectif " +
            "ORDER BY SUM(r.valeurRendement) DESC")
    List<ProductionByActivityDTO> getProductionByActivity(@Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.pfe.webapp.dto.stats.ProductionTrendDTO(" +
            "CONCAT(SUBSTRING(CAST(r.date AS string), 1, 7)), SUM(r.valeurRendement)) " +
            "FROM Rendement r " +
            "GROUP BY SUBSTRING(CAST(r.date AS string), 1, 7) " +
            "ORDER BY SUBSTRING(CAST(r.date AS string), 1, 7)")
    List<ProductionTrendDTO> getProductionTrend();

    @Query("SELECT new com.pfe.webapp.dto.stats.ProductionTrendDTO(" +
            "CONCAT(SUBSTRING(CAST(r.date AS string), 1, 7)), SUM(r.valeurRendement)) " +
            "FROM Rendement r " +
            "WHERE r.date BETWEEN :startDate AND :endDate " +
            "GROUP BY SUBSTRING(CAST(r.date AS string), 1, 7) " +
            "ORDER BY SUBSTRING(CAST(r.date AS string), 1, 7)")
    List<ProductionTrendDTO> getProductionTrend(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.pfe.webapp.dto.stats.ProductionByMissionDTO(m.codeMission, SUM(r.valeurRendement)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.mission m " +
            "GROUP BY m.codeMission " +
            "ORDER BY SUM(r.valeurRendement) DESC")
    List<ProductionByMissionDTO> getProductionByMission();

    @Query("SELECT new com.pfe.webapp.dto.stats.ProductionByMissionDTO(m.codeMission, SUM(r.valeurRendement)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.mission m " +
            "WHERE r.date BETWEEN :startDate AND :endDate " +
            "GROUP BY m.codeMission " +
            "ORDER BY SUM(r.valeurRendement) DESC")
    List<ProductionByMissionDTO> getProductionByMission(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    // ========== TABLE QUERIES - FIXED (without ROW_NUMBER) ==========

    // ✅ FIXED: Top 5 Teams - uses simple query without ROW_NUMBER
    @Query("SELECT new com.pfe.webapp.dto.stats.TopTeamDTO(e.nom, SUM(r.valeurRendement)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.equipe e " +
            "GROUP BY e.nom " +
            "ORDER BY SUM(r.valeurRendement) DESC")
    List<TopTeamDTO> getTop5Teams();

    // ✅ FIXED: Top 5 Activities - uses simple query without ROW_NUMBER
    @Query("SELECT new com.pfe.webapp.dto.stats.TopActivityDTO(a.codeActive, a.objectif, SUM(r.valeurRendement)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.active a " +
            "WHERE a IS NOT NULL " +
            "GROUP BY a.codeActive, a.objectif " +
            "ORDER BY SUM(r.valeurRendement) DESC")
    List<TopActivityDTO> getTop5Activities();

    // ========== ADVANCED QUERIES ==========

    @Query("SELECT new com.pfe.webapp.dto.stats.ProductivityByTeamDTO(" +
            "e.nom, SUM(r.valeurRendement), SUM(r.dureeHeures)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.equipe e " +
            "GROUP BY e.nom " +
            "ORDER BY (SUM(r.valeurRendement) / NULLIF(SUM(r.dureeHeures), 0)) DESC")
    List<ProductivityByTeamDTO> getProductivityByTeam();

    @Query("SELECT new com.pfe.webapp.dto.stats.ProductivityByActivityDTO(" +
            "a.codeActive, a.objectif, SUM(r.valeurRendement), SUM(r.dureeHeures)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.active a " +
            "WHERE a IS NOT NULL " +
            "GROUP BY a.codeActive, a.objectif " +
            "ORDER BY (SUM(r.valeurRendement) / NULLIF(SUM(r.dureeHeures), 0)) DESC")
    List<ProductivityByActivityDTO> getProductivityByActivity();

    // ========== FILTER BY MISSION ==========

    @Query("SELECT new com.pfe.webapp.dto.stats.ProductionByTeamDTO(e.nom, SUM(r.valeurRendement)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.equipe e " +
            "WHERE ae.mission.id = :missionId " +
            "GROUP BY e.nom " +
            "ORDER BY SUM(r.valeurRendement) DESC")
    List<ProductionByTeamDTO> getProductionByTeamByMission(@Param("missionId") Long missionId);

    @Query("SELECT new com.pfe.webapp.dto.stats.ProductionByActivityDTO(a.codeActive, a.objectif, SUM(r.valeurRendement)) " +
            "FROM Rendement r JOIN r.affectationEquipe ae JOIN ae.active a " +
            "WHERE ae.mission.id = :missionId AND a IS NOT NULL " +
            "GROUP BY a.codeActive, a.objectif " +
            "ORDER BY SUM(r.valeurRendement) DESC")
    List<ProductionByActivityDTO> getProductionByActivityByMission(@Param("missionId") Long missionId);
}