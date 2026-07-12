package com.pfe.webapp.repository.ressource;

import com.pfe.webapp.entity.ressource.Motif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MotifRepository extends JpaRepository<Motif, Long> {

    // Find by code (exact match) - for createOrFind
    Optional<Motif> findByCode(String code);

    // Find by code containing (case insensitive)
    List<Motif> findByCodeContainingIgnoreCase(String code);

    // Find by description containing
    List<Motif> findByDescriptionContainingIgnoreCase(String description);

    // Check if code exists
    boolean existsByCode(String code);

    // Get all motifs ordered by code
    @Query("SELECT m FROM Motif m ORDER BY m.code ASC")
    List<Motif> findAllOrderByCode();

    // Get motifs that have consumptions for a specific mission
    @Query("SELECT DISTINCT m FROM Motif m JOIN m.consommations c WHERE c.mission.id = :missionId")
    List<Motif> findMotifsUsedInMission(@Param("missionId") Long missionId);

    // Count consumptions by motif
    @Query("SELECT m.code, COUNT(c) FROM Motif m LEFT JOIN m.consommations c GROUP BY m.code")
    List<Object[]> countConsumptionsByMotif();
}