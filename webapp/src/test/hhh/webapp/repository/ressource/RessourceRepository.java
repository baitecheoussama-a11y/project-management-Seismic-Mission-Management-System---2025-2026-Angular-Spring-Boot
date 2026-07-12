// repository/ressources/RessourceRepository.java
package com.pfe.webapp.repository.ressource;

import com.pfe.webapp.entity.ressource.Ressource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RessourceRepository extends JpaRepository<Ressource, Long> {

    // Find by titre
    List<Ressource> findByTitreContainingIgnoreCase(String titre);

    // Find by type
    List<Ressource> findByTypeRessourceIdTypeRessource(Long typeId);

    // Find by category
    List<Ressource> findByTypeRessource_CategorieRessource_IdCategorieRessource(Long categoryId);

    // Find by stock less than threshold
    List<Ressource> findByQuantiteLessThan(Double threshold);

    // Find by dateAchat after
    List<Ressource> findByDateAchatAfter(LocalDate date);

    // Find by unit
    List<Ressource> findByUnite(String unite);

    // Get total stock value
    @Query("SELECT COALESCE(SUM(r.quantite * r.cout), 0) FROM Ressource r")
    Double getTotalStockValue();

    // Get resources with low stock (less than 10% of initial or absolute threshold)
    @Query("SELECT r FROM Ressource r WHERE r.quantite < :threshold")
    List<Ressource> findResourcesWithLowStock(@Param("threshold") Double threshold);

    // Get resources that have been consumed in a mission
    @Query("SELECT DISTINCT r FROM Ressource r JOIN r.consommations c WHERE c.mission.id = :missionId")
    List<Ressource> findResourcesConsumedInMission(@Param("missionId") Long missionId);

    // Get resources never consumed
    @Query("SELECT r FROM Ressource r WHERE r.consommations IS EMPTY")
    List<Ressource> findResourcesNeverConsumed();

    // Get total consumed quantity for a resource
    @Query("SELECT COALESCE(SUM(c.valeur), 0) FROM Consommation c WHERE c.ressource.idRessource = :ressourceId")
    Double getTotalConsumedQuantity(@Param("ressourceId") Long ressourceId);

    // Get resource with consumption details for a mission
    @Query("SELECT r, COALESCE(SUM(c.valeur), 0) FROM Ressource r LEFT JOIN r.consommations c ON c.mission.id = :missionId WHERE r.idRessource = :ressourceId GROUP BY r.idRessource")
    List<Object[]> getResourceWithMissionConsumption(@Param("ressourceId") Long ressourceId, @Param("missionId") Long missionId);
}