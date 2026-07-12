package com.pfe.webapp.repository.team;


import com.pfe.webapp.entity.Active;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActiveRepository extends JpaRepository<Active, Long> {

    Optional<Active> findByCodeActive(String codeActive);

    boolean existsByCodeActive(String codeActive);

    @Query("SELECT a FROM Active a LEFT JOIN FETCH a.equipes")
    List<Active> findAllWithEquipes();


    @Query("SELECT a FROM Active a WHERE a.id NOT IN (SELECT DISTINCT ae.active.id FROM AffectationEquipe ae WHERE ae.mission.id = :missionId)")
    List<Active> findActivesNotAssignedToMission(@Param("missionId") Long missionId);

    @Query("SELECT DISTINCT a FROM Active a LEFT JOIN FETCH a.affectationEquipes ae WHERE ae.equipe.id = :equipeId")
    List<Active> findActivesByEquipeId(@Param("equipeId") Long equipeId);

    // ✅ NEW: Get actives that have NO mission assignment at all
    @Query("SELECT a FROM Active a WHERE a.id NOT IN (SELECT DISTINCT ae.active.id FROM AffectationEquipe ae)")
    List<Active> findActivesWithoutMissionAssignment();

    // Get actives that are either not assigned to any mission OR assigned to this specific mission
    @Query("SELECT a FROM Active a " +
            "WHERE NOT EXISTS (SELECT 1 FROM AffectationEquipe ae " +
            "WHERE ae.active.id = a.id AND ae.mission.id != :missionId) " +
            "ORDER BY a.codeActive ASC")
    List<Active> findActivesAvailableForMission(@Param("missionId") Long missionId);
}