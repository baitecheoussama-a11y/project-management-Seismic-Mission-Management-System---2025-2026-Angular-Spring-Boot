package com.pfe.webapp.repository;

import com.pfe.webapp.entity.materiel.Reparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReparationRepository extends JpaRepository<Reparation, Long> {

    List<Reparation> findByMaterielIdMateriel(Long materielId);

    // Get pending breakdowns (STOCK + MISSION)
    @Query("SELECT r FROM Reparation r WHERE r.materiel.idMateriel = :materielId AND r.status = 'PENDING'")
    List<Reparation> findPendingByMaterielId(@Param("materielId") Long materielId);

    // Get completed repairs
    @Query("SELECT r FROM Reparation r WHERE r.materiel.idMateriel = :materielId AND r.status = 'COMPLETED'")
    List<Reparation> findByMaterielIdAndDateReparationNotNull(@Param("materielId") Long materielId);

    // Get repairs by materiel AND mission
    @Query("SELECT r FROM Reparation r WHERE r.materiel.idMateriel = :materielId AND r.mission.id = :missionId")
    List<Reparation> findByMaterielIdAndMissionId(@Param("materielId") Long materielId, @Param("missionId") Long missionId);

    // Get pending repairs for a specific mission
    @Query("SELECT r FROM Reparation r WHERE r.materiel.idMateriel = :materielId AND r.mission.id = :missionId AND r.status = 'PENDING'")
    List<Reparation> findPendingByMaterielIdAndMissionId(@Param("materielId") Long materielId, @Param("missionId") Long missionId);

    // Get ongoing repairs for a specific mission (IN_PROGRESS or SENT)
    @Query("SELECT r FROM Reparation r WHERE r.materiel.idMateriel = :materielId AND r.mission.id = :missionId AND (r.status = 'IN_PROGRESS' OR r.status = 'SENT')")
    List<Reparation> findOngoingByMaterielIdAndMissionId(@Param("materielId") Long materielId, @Param("missionId") Long missionId);

    // Get completed repairs for a specific mission
    @Query("SELECT r FROM Reparation r WHERE r.materiel.idMateriel = :materielId AND r.mission.id = :missionId AND r.status = 'COMPLETED'")
    List<Reparation> findCompletedByMaterielIdAndMissionId(@Param("materielId") Long materielId, @Param("missionId") Long missionId);

    // ✅ MODIFIED: Count broken by affectation and mission (PENDING only) - بدون quantity
    @Query("SELECT COUNT(r) FROM Reparation r WHERE r.affectationId = :affectationId AND r.mission.id = :missionId AND r.status = 'PENDING'")
    Integer countBrokenByAffectationIdAndMissionId(@Param("affectationId") Long affectationId, @Param("missionId") Long missionId);

    // ✅ MODIFIED: Count in repair by affectation and mission (IN_PROGRESS or SENT) - بدون quantity
    @Query("SELECT COUNT(r) FROM Reparation r WHERE r.affectationId = :affectationId AND r.mission.id = :missionId AND (r.status = 'IN_PROGRESS' OR r.status = 'SENT')")
    Integer countInRepairByAffectationIdAndMissionId(@Param("affectationId") Long affectationId, @Param("missionId") Long missionId);

    // ✅ MODIFIED: Count broken by affectation - بدون quantity
    @Query("SELECT COUNT(r) FROM Reparation r WHERE r.affectationId = :affectationId")
    Integer countBrokenByAffectationId(@Param("affectationId") Long affectationId);
}