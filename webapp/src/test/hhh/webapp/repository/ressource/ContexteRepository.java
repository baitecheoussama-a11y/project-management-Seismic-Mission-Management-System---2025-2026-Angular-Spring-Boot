package com.pfe.webapp.repository.ressource;

import com.pfe.webapp.entity.ressource.Contexte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContexteRepository extends JpaRepository<Contexte, Long> {

    // Find by titre (exact match) - for createOrFind
    Optional<Contexte> findByTitre(String titre);

    // Find by titre containing (case insensitive)
    List<Contexte> findByTitreContainingIgnoreCase(String titre);

    // Find by description containing
    List<Contexte> findByDescriptionContainingIgnoreCase(String description);

    // Get all contextes ordered by titre
    @Query("SELECT c FROM Contexte c ORDER BY c.titre ASC")
    List<Contexte> findAllOrderByTitre();

    // Get contextes that have consumptions for a specific mission
    @Query("SELECT DISTINCT c FROM Contexte c JOIN c.consommations cons WHERE cons.mission.id = :missionId")
    List<Contexte> findContextesUsedInMission(@Param("missionId") Long missionId);

    // Get contextes with most consumptions
    @Query("SELECT c.titre, COUNT(cons) FROM Contexte c LEFT JOIN c.consommations cons GROUP BY c.id ORDER BY COUNT(cons) DESC")
    List<Object[]> findContextesWithConsumptionCount();

    // Search by titre or description
    @Query("SELECT c FROM Contexte c WHERE LOWER(c.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Contexte> searchByTerm(@Param("searchTerm") String searchTerm);
}