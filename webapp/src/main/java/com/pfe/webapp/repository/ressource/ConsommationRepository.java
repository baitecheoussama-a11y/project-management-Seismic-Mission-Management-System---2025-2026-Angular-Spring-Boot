package com.pfe.webapp.repository.ressource;

import com.pfe.webapp.dto.stats.ConsommationStatsDTO;
import com.pfe.webapp.dto.stats.MonthlyConsommationDTO;
import com.pfe.webapp.dto.stats.RessourceCostStatsDTO;
import com.pfe.webapp.entity.ressource.Consommation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsommationRepository extends JpaRepository<Consommation, Long> {

    // Find by mission ID
    List<Consommation> findByMissionId(Long missionId);

    // Find by resource ID using proper path
    List<Consommation> findByRessource_IdRessource(Long ressourceId);

    // Find by motif ID
    List<Consommation> findByMotif_IdMotif(Long motifId);

    // Find by contexte ID
    List<Consommation> findByContexte_IdContexte(Long contexteId);

    // Find by date range
    List<Consommation> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // Find by mission and resource
    List<Consommation> findByMissionIdAndRessource_IdRessource(Long missionId, Long ressourceId);

    // Sum of consumed quantity by mission
    @Query("SELECT COALESCE(SUM(c.valeur), 0) FROM Consommation c WHERE c.mission.id = :missionId")
    Double sumValeurByMissionId(@Param("missionId") Long missionId);

    // Sum of consumed quantity by resource across all missions
    @Query("SELECT COALESCE(SUM(c.valeur), 0) FROM Consommation c WHERE c.ressource.idRessource = :ressourceId")
    Double sumValeurByRessourceId(@Param("ressourceId") Long ressourceId);

    // Sum of consumed quantity by mission and resource
    @Query("SELECT COALESCE(SUM(c.valeur), 0) FROM Consommation c WHERE c.mission.id = :missionId AND c.ressource.idRessource = :ressourceId")
    Double sumValeurByMissionIdAndRessourceId(@Param("missionId") Long missionId, @Param("ressourceId") Long ressourceId);

    // Find consommations by mission with resource details (JPQL join fetch)
    @Query("SELECT c FROM Consommation c LEFT JOIN FETCH c.ressource LEFT JOIN FETCH c.motif LEFT JOIN FETCH c.contexte WHERE c.mission.id = :missionId ORDER BY c.date DESC")
    List<Consommation> findByMissionIdWithDetails(@Param("missionId") Long missionId);

    // Get total cost by mission (using native query for performance)
    @Query(value = "SELECT COALESCE(SUM(c.valeur * r.cout), 0) FROM consommation c JOIN ressource r ON c.id_ressource = r.id_ressource WHERE c.id_mission = :missionId", nativeQuery = true)
    Double getTotalCostByMission(@Param("missionId") Long missionId);

    // Get consumption summary by resource for a mission
    @Query("SELECT c.ressource.idRessource, COALESCE(SUM(c.valeur), 0) FROM Consommation c WHERE c.mission.id = :missionId GROUP BY c.ressource.idRessource")
    List<Object[]> getConsumptionSummaryByMission(@Param("missionId") Long missionId);






    // ========== CONSOMMATION BY RESSOURCE ==========
    @Query("SELECT new com.pfe.webapp.dto.stats.ConsommationStatsDTO(r.titre, SUM(c.valeur)) " +
            "FROM Consommation c JOIN c.ressource r " +
            "WHERE c.date BETWEEN :startDate AND :endDate " +
            "GROUP BY r.titre " +
            "ORDER BY SUM(c.valeur) DESC")
    List<ConsommationStatsDTO> getConsommationByRessource(@Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    // ========== CONSOMMATION BY MISSION ==========
    @Query("SELECT new com.pfe.webapp.dto.stats.ConsommationStatsDTO(m.codeMission, SUM(c.valeur)) " +
            "FROM Consommation c JOIN c.mission m " +
            "WHERE c.date BETWEEN :startDate AND :endDate " +
            "GROUP BY m.codeMission " +
            "ORDER BY SUM(c.valeur) DESC")
    List<ConsommationStatsDTO> getConsommationByMission(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    // ========== COST BY RESSOURCE ==========
    @Query("SELECT new com.pfe.webapp.dto.stats.RessourceCostStatsDTO(r.titre, SUM(c.valeur), SUM(c.valeur * r.cout)) " +
            "FROM Consommation c JOIN c.ressource r " +
            "WHERE c.date BETWEEN :startDate AND :endDate " +
            "GROUP BY r.titre " +
            "ORDER BY SUM(c.valeur * r.cout) DESC")
    List<RessourceCostStatsDTO> getCostByRessource(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    // ========== CONSOMMATION BY MONTH ==========
    @Query("SELECT new com.pfe.webapp.dto.stats.MonthlyConsommationDTO(" +
            "CONCAT(SUBSTRING(CAST(c.date AS string), 1, 7)), SUM(c.valeur)) " +
            "FROM Consommation c " +
            "WHERE c.date BETWEEN :startDate AND :endDate " +
            "GROUP BY SUBSTRING(CAST(c.date AS string), 1, 7) " +
            "ORDER BY SUBSTRING(CAST(c.date AS string), 1, 7)")
    List<MonthlyConsommationDTO> getConsommationByMonth(@Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);

    // ========== TOTAL COST ==========
    @Query("SELECT SUM(c.valeur * r.cout) FROM Consommation c JOIN c.ressource r " +
            "WHERE c.date BETWEEN :startDate AND :endDate")
    Double getTotalCost(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

    // ========== CONSOMMATION BY TYPE RESSOURCE ==========
    @Query("SELECT new com.pfe.webapp.dto.stats.ConsommationStatsDTO(t.nom, SUM(c.valeur)) " +
            "FROM Consommation c JOIN c.ressource r JOIN r.typeRessource t " +
            "WHERE c.date BETWEEN :startDate AND :endDate " +
            "GROUP BY t.nom " +
            "ORDER BY SUM(c.valeur) DESC")
    List<ConsommationStatsDTO> getConsommationByType(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    // ========== COST BY TYPE RESSOURCE ==========
    @Query("SELECT new com.pfe.webapp.dto.stats.RessourceCostStatsDTO(t.nom, SUM(c.valeur), SUM(c.valeur * r.cout)) " +
            "FROM Consommation c JOIN c.ressource r JOIN r.typeRessource t " +
            "WHERE c.date BETWEEN :startDate AND :endDate " +
            "GROUP BY t.nom " +
            "ORDER BY SUM(c.valeur * r.cout) DESC")
    List<RessourceCostStatsDTO> getCostByType(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    // ========== TOP 5 RESSOURCES (by consumption) ==========
    @Query("SELECT new com.pfe.webapp.dto.stats.RessourceCostStatsDTO(r.titre, SUM(c.valeur), SUM(c.valeur * r.cout)) " +
            "FROM Consommation c JOIN c.ressource r " +
            "WHERE c.date BETWEEN :startDate AND :endDate " +
            "GROUP BY r.titre " +
            "ORDER BY SUM(c.valeur) DESC")
    List<RessourceCostStatsDTO> getTop5Ressources(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    // repository/ressource/ConsommationRepository.java - أضف هذه الطريقة

    // Find by date
    List<Consommation> findByDate(LocalDate date);


}