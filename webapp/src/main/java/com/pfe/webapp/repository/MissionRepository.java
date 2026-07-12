// MissionRepository.java
package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.entity.TypeMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    // Find by code
    Mission findByCodeMission(String codeMission);

    // Find by methodology
    List<Mission> findByMethodologie(TypeMission methodologie);

    // Get mission with all consumptions
    @Query("SELECT DISTINCT m FROM Mission m LEFT JOIN FETCH m.consommations c LEFT JOIN FETCH c.ressource LEFT JOIN FETCH c.motif LEFT JOIN FETCH c.contexte WHERE m.id = :id")
    Mission findByIdWithConsumptions(@Param("id") Long id);

    // Get missions that consumed a specific resource
    @Query("SELECT DISTINCT m FROM Mission m JOIN m.consommations c WHERE c.ressource.idRessource = :ressourceId")
    List<Mission> findMissionsByResourceConsumption(@Param("ressourceId") Long ressourceId);

    // Get total consumption value for a mission
    @Query("SELECT COALESCE(SUM(c.valeur * c.ressource.cout), 0) FROM Mission m JOIN m.consommations c WHERE m.id = :missionId")
    Double getTotalConsumptionCost(@Param("missionId") Long missionId);

    // Get mission consumption summary
    @Query("SELECT m.id, m.codeMission, COALESCE(SUM(c.valeur), 0), COALESCE(SUM(c.valeur * c.ressource.cout), 0) FROM Mission m LEFT JOIN m.consommations c GROUP BY m.id")
    List<Object[]> getMissionConsumptionSummary();
}