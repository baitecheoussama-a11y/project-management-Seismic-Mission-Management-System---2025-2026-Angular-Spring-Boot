package com.pfe.webapp.repository.incident;

import com.pfe.webapp.entity.Incident;
import com.pfe.webapp.entity.NiveauGravite;
import com.pfe.webapp.entity.TypeIncident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {


    List<Incident> findByEtatMedicalId(Long etatMedicalId);
    List<Incident> findByType(TypeIncident type);
    List<Incident> findByNiveauGravite(NiveauGravite niveauGravite);



    List<Incident> findByEmployeId(Long employeId);

    List<Incident> findByType(String type);

    List<Incident> findByNiveauGravite(String niveauGravite);

    List<Incident> findByDateIncidentBetween(LocalDate startDate, LocalDate endDate);

    List<Incident> findByDateIncident(LocalDate date);

    @Query("SELECT i FROM Incident i WHERE i.dateIncident >= :date ORDER BY i.dateIncident DESC")
    List<Incident> findRecentIncidents(@Param("date") LocalDate date);

    @Query("SELECT i FROM Incident i WHERE " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.type) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Incident> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    long countByDateIncidentBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT i.niveauGravite, COUNT(i) FROM Incident i GROUP BY i.niveauGravite")
    List<Object[]> countByNiveauGravite();

    @Query("SELECT i.type, COUNT(i) FROM Incident i GROUP BY i.type")
    List<Object[]> countByType();

    // IncidentRepository.java - أضف هذه الدوال

    // الحصول على حوادث الموظفين لمهمة معينة
    @Query("SELECT i FROM Incident i WHERE i.employe.id IN " +
            "(SELECT ae.employe.id FROM AffectationEmploye ae WHERE ae.mission.id = :missionId)")
    List<Incident> findIncidentsByMissionId(@Param("missionId") Long missionId);

    // مع Pagination
    @Query("SELECT i FROM Incident i WHERE i.employe.id IN " +
            "(SELECT ae.employe.id FROM AffectationEmploye ae WHERE ae.mission.id = :missionId)")
    Page<Incident> findIncidentsByMissionId(@Param("missionId") Long missionId, Pageable pageable);

    // البحث في حوادث مهمة معينة
    @Query("SELECT i FROM Incident i WHERE i.employe.id IN " +
            "(SELECT ae.employe.id FROM AffectationEmploye ae WHERE ae.mission.id = :missionId) AND " +
            "(LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(i.type) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Incident> searchIncidentsByMissionId(@Param("missionId") Long missionId,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);
}