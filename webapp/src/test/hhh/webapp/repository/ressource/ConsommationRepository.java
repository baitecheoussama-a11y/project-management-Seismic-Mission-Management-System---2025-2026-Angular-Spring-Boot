package com.pfe.webapp.repository.ressource;

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
}