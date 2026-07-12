// repository/RendementRepository.java
package com.pfe.webapp.repository.rapport;

import com.pfe.webapp.entity.Rendement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RendementRepository extends JpaRepository<Rendement, Long> {

    List<Rendement> findByRapportId(Long rapportId);

    List<Rendement> findByAffectationEquipeId(Long affectationEquipeId);

    @Query("SELECT AVG(r.valeurRendement) FROM Rendement r WHERE r.affectationEquipe.id = :affectationEquipeId")
    Double getAverageRendementByAffectationEquipe(@Param("affectationEquipeId") Long affectationEquipeId);

    @Query("SELECT SUM(r.valeurRendement) FROM Rendement r WHERE r.rapport.id = :rapportId")
    Double getTotalRendementByRapport(@Param("rapportId") Long rapportId);

    @Query("SELECT r FROM Rendement r WHERE r.affectationEquipe.equipe.id = :equipeId ORDER BY r.date DESC")
    List<Rendement> findRendementsByEquipeId(@Param("equipeId") Long equipeId);

    @Query("SELECT AVG(r.valeurRendement) FROM Rendement r WHERE r.affectationEquipe.equipe.id = :equipeId")
    Double getAverageRendementByEquipeId(@Param("equipeId") Long equipeId);




    // ✅ NEW: Get rendements by rapport ID and equipe ID
    @Query("SELECT r FROM Rendement r WHERE r.rapport.id = :rapportId AND r.affectationEquipe.equipe.id = :equipeId")
    List<Rendement> findByRapportIdAndEquipeId(@Param("rapportId") Long rapportId, @Param("equipeId") Long equipeId);

    // ✅ NEW: Get rendements by rapport ID and mission ID (through affectationEquipe)
    @Query("SELECT r FROM Rendement r WHERE r.rapport.id = :rapportId AND r.affectationEquipe.mission.id = :missionId")
    List<Rendement> findByRapportIdAndMissionId(@Param("rapportId") Long rapportId, @Param("missionId") Long missionId);


    // RendementRepository.java - أضف هذه الدوال

    @Query("SELECT r FROM Rendement r WHERE r.affectationEquipe.equipe.id = :equipeId AND r.affectationEquipe.mission.id = :missionId ORDER BY r.date DESC")
    List<Rendement> findByEquipeIdAndMissionId(@Param("equipeId") Long equipeId, @Param("missionId") Long missionId);


}