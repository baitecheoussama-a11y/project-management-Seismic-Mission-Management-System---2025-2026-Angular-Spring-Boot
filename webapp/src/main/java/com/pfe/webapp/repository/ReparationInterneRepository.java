package com.pfe.webapp.repository;

import com.pfe.webapp.entity.materiel.ReparationInterne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReparationInterneRepository extends JpaRepository<ReparationInterne, Long> {

    // ✅ Find by materiel ID
    @Query("SELECT r FROM ReparationInterne r WHERE r.materiel.idMateriel = :materielId")
    List<ReparationInterne> findByMaterielIdMateriel(@Param("materielId") Long materielId);

    // ✅ Find ongoing internal repairs (status = 'IN_PROGRESS')
    @Query("SELECT r FROM ReparationInterne r WHERE r.materiel.idMateriel = :materielId AND r.status = 'IN_PROGRESS'")
    List<ReparationInterne> findOngoingByMaterielId(@Param("materielId") Long materielId);

    // ✅ Find by materiel ID and status
    @Query("SELECT r FROM ReparationInterne r WHERE r.materiel.idMateriel = :materielId AND r.status = :status")
    List<ReparationInterne> findByMaterielIdAndStatus(@Param("materielId") Long materielId, @Param("status") String status);
}