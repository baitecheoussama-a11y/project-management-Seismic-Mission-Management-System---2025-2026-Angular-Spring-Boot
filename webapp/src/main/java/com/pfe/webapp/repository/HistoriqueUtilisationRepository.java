// HistoriqueUtilisationRepository.java - CORRECTED
package com.pfe.webapp.repository;

import com.pfe.webapp.entity.materiel.HistoriqueUtilisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistoriqueUtilisationRepository extends JpaRepository<HistoriqueUtilisation, Long> {

    // Fixed: Correct path through AffectationMateriel to Materiel
    @Query("SELECT h FROM HistoriqueUtilisation h WHERE h.affectation.materiel.idMateriel = :materielId")
    List<HistoriqueUtilisation> findByMaterielId(@Param("materielId") Long materielId);

    // Alternative method if you prefer method naming (but this works too)
    List<HistoriqueUtilisation> findByAffectationMaterielIdMateriel(Long materielId);
}