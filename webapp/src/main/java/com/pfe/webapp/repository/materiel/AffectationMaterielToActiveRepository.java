// src/main/java/com/pfe/webapp/repository/materiel/AffectationMaterielToActiveRepository.java
package com.pfe.webapp.repository.materiel;

import com.pfe.webapp.entity.materiel.AffectationMaterielToActive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AffectationMaterielToActiveRepository extends JpaRepository<AffectationMaterielToActive, Long> {

    @Query("SELECT a FROM AffectationMaterielToActive a WHERE a.materiel.idMateriel = :materielId")
    List<AffectationMaterielToActive> findByMaterielId(@Param("materielId") Long materielId);

    @Query("SELECT a FROM AffectationMaterielToActive a WHERE a.active.id = :activeId")
    List<AffectationMaterielToActive> findByActiveId(@Param("activeId") Long activeId);

    @Query("SELECT a FROM AffectationMaterielToActive a WHERE a.materiel.idMateriel = :materielId AND a.active.id = :activeId")
    Optional<AffectationMaterielToActive> findByMaterielIdAndActiveId(@Param("materielId") Long materielId,
                                                                      @Param("activeId") Long activeId);

    @Query("SELECT a FROM AffectationMaterielToActive a LEFT JOIN FETCH a.materiel LEFT JOIN FETCH a.active WHERE a.materiel.idMateriel = :materielId")
    List<AffectationMaterielToActive> findByMaterielIdWithDetails(@Param("materielId") Long materielId);

    @Query("SELECT a FROM AffectationMaterielToActive a LEFT JOIN FETCH a.materiel LEFT JOIN FETCH a.active WHERE a.active.id = :activeId")
    List<AffectationMaterielToActive> findByActiveIdWithDetails(@Param("activeId") Long activeId);

    @Query("SELECT a FROM AffectationMaterielToActive a WHERE a.materiel.idMateriel = :materielId AND (a.dateFin IS NULL OR a.dateFin >= CURRENT_DATE)")
    List<AffectationMaterielToActive> findActiveByMaterielId(@Param("materielId") Long materielId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AffectationMaterielToActive a WHERE a.materiel.idMateriel = :materielId AND a.active.id = :activeId")
    boolean existsByMaterielIdAndActiveId(@Param("materielId") Long materielId,
                                          @Param("activeId") Long activeId);

    @Modifying
    @Query("DELETE FROM AffectationMaterielToActive a WHERE a.materiel.idMateriel = :materielId AND a.active.id = :activeId")
    void deleteByMaterielIdAndActiveId(@Param("materielId") Long materielId,
                                       @Param("activeId") Long activeId);

    // ✅ FIXED: Get materials assigned to a project through Active -> AffectationEquipe -> Project
    @Query("SELECT a FROM AffectationMaterielToActive a " +
            "JOIN a.active act " +
            "JOIN act.affectationEquipes ae " +
            "WHERE ae.project.id = :projectId")
    List<AffectationMaterielToActive> findByActiveProjectId(@Param("projectId") Long projectId);
}