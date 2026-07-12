package com.pfe.webapp.repository;

import com.pfe.webapp.entity.materiel.AffectationMateriel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AffectationMaterielRepository extends JpaRepository<AffectationMateriel, Long> {

    List<AffectationMateriel> findByMaterielIdMateriel(Long materielId);

    @Query("SELECT a FROM AffectationMateriel a WHERE a.mission.id = :missionId")
    List<AffectationMateriel> findByMissionId(@Param("missionId") Long missionId);

    // ✅ التحقق إذا كان الماتريال عنده تعيين نشط في فترة معينة
    @Query("SELECT a FROM AffectationMateriel a WHERE a.materiel.idMateriel = :materielId " +
            "AND a.dateDebut <= :endDate AND (a.dateFin IS NULL OR a.dateFin >= :startDate)")
    List<AffectationMateriel> findActiveInPeriod(
            @Param("materielId") Long materielId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // ✅ إضافة دالة للبحث عن التعيينات النشطة حاليا
    @Query("SELECT a FROM AffectationMateriel a WHERE a.materiel.idMateriel = :materielId " +
            "AND (a.dateFin IS NULL OR a.dateFin >= CURRENT_DATE)")
    List<AffectationMateriel> findActiveByMaterielId(@Param("materielId") Long materielId);

    void deleteByMaterielIdMateriel(Long materielId);

    void deleteByMissionId(Long missionId);


}