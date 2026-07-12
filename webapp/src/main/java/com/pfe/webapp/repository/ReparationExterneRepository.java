package com.pfe.webapp.repository;

import com.pfe.webapp.entity.materiel.ReparationExterne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReparationExterneRepository extends JpaRepository<ReparationExterne, Long> {

    // ✅ Find by materiel ID
    @Query("SELECT r FROM ReparationExterne r WHERE r.materiel.idMateriel = :materielId")
    List<ReparationExterne> findByMaterielIdMateriel(@Param("materielId") Long materielId);

    // ✅ Find sent external repairs (status = 'SENT')
    @Query("SELECT r FROM ReparationExterne r WHERE r.materiel.idMateriel = :materielId AND r.status = 'SENT'")
    List<ReparationExterne> findSentByMaterielId(@Param("materielId") Long materielId);

    // ✅ Find by materiel ID and status
    @Query("SELECT r FROM ReparationExterne r WHERE r.materiel.idMateriel = :materielId AND r.status = :status")
    List<ReparationExterne> findByMaterielIdAndStatus(@Param("materielId") Long materielId, @Param("status") String status);
}