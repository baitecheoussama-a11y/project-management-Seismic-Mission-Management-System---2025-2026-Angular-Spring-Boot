// repository/AffectationEmployeRepository.java
package com.pfe.webapp.repository;

import com.pfe.webapp.entity.AffectationEmploye;
import com.pfe.webapp.entity.Employe;
import com.pfe.webapp.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AffectationEmployeRepository extends JpaRepository<AffectationEmploye, Long> {

    List<AffectationEmploye> findByMissionId(Long missionId);

    @Query("SELECT a FROM AffectationEmploye a WHERE a.mission.id = :missionId AND (a.dateFin IS NULL OR a.dateFin >= :date)")


    List<AffectationEmploye> findByMissionIdAndDateFinAfterOrDateFinNull(@Param("missionId") Long missionId,
                                                                         @Param("date") LocalDate date);

    @Query("SELECT a FROM AffectationEmploye a WHERE a.employe.id = :employeId AND (a.dateFin IS NULL OR a.dateFin >= :date)")
    List<AffectationEmploye> findByEmployeIdAndDateFinAfterOrDateFinNull(@Param("employeId") Long employeId,
                                                                         @Param("date") LocalDate date);

    @Query("SELECT a FROM AffectationEmploye a WHERE a.mission.id = :missionId AND a.employe.id = :employeId AND (a.dateFin IS NULL OR a.dateFin >= :date)")
    Optional<AffectationEmploye> findByMissionIdAndEmployeIdAndDateFinAfterOrDateFinNull(@Param("missionId") Long missionId,
                                                                                         @Param("employeId") Long employeId,
                                                                                         @Param("date") LocalDate date);

    @Query("SELECT a FROM AffectationEmploye a WHERE a.dateFin IS NULL OR a.dateFin >= :date")
    List<AffectationEmploye> findActiveAffectations(@Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM AffectationEmploye a WHERE a.equipe.id = :equipeId AND (a.dateFin IS NULL OR a.dateFin >= :date)")
    long countActiveMembersByEquipe(@Param("equipeId") Long equipeId, @Param("date") LocalDate date);


    // Find by equipe id with active assignments
    @Query("SELECT a FROM AffectationEmploye a WHERE a.equipe.id = :equipeId AND (a.dateFin IS NULL OR a.dateFin >= :date)")
    List<AffectationEmploye> findByEquipeIdAndDateFinAfterOrDateFinNull(@Param("equipeId") Long equipeId,
                                                                        @Param("date") LocalDate date);

    // Find by equipe id and employe id with active assignments
    @Query("SELECT a FROM AffectationEmploye a WHERE a.equipe.id = :equipeId AND a.employe.id = :employeId AND (a.dateFin IS NULL OR a.dateFin >= :date)")
    List<AffectationEmploye> findByEquipeIdAndEmployeIdAndDateFinAfterOrDateFinNull(@Param("equipeId") Long equipeId,
                                                                                    @Param("employeId") Long employeId,
                                                                                    @Param("date") LocalDate date);

    // ✅ Get active mission for an employee (considering dateFin can be null)
    @Query("SELECT a FROM AffectationEmploye a WHERE a.employe.id = :employeId AND a.dateDebut <= :currentDate AND (a.dateFin IS NULL OR a.dateFin >= :currentDate)")


    Optional<AffectationEmploye> findActiveMissionByEmployeId(@Param("employeId") Long employeId,
                                                              @Param("currentDate") LocalDate currentDate);

    // ✅ Check if employee has an active mission
    @Query("SELECT COUNT(a) > 0 FROM AffectationEmploye a WHERE a.employe.id = :employeId AND a.dateDebut <= :currentDate AND (a.dateFin IS NULL OR a.dateFin >= :currentDate)")
    boolean hasActiveMission(@Param("employeId") Long employeId, @Param("currentDate") LocalDate currentDate);

    List<AffectationEmploye> findByEquipeIdAndMissionId(@Param("equipeId") Long equipeId,
                                                        @Param("missionId") Long missionId);


    // repository/AffectationEmployeRepository.java - Add these methods

    @Query("SELECT ae FROM AffectationEmploye ae WHERE ae.equipe.id = :equipeId AND ae.mission.id = :missionId AND (ae.dateFin IS NULL OR ae.dateFin >= :currentDate)")
    List<AffectationEmploye> findByEquipeIdAndMissionIdAndDateFinAfterOrDateFinNull(
            @Param("equipeId") Long equipeId,
            @Param("missionId") Long missionId,
            @Param("currentDate") LocalDate currentDate);

    @Query("SELECT COUNT(ae) FROM AffectationEmploye ae WHERE ae.equipe.id = :equipeId AND ae.mission.id = :missionId AND (ae.dateFin IS NULL OR ae.dateFin >= :currentDate)")
    long countActiveMembersByEquipeAndMission(@Param("equipeId") Long equipeId,
                                              @Param("missionId") Long missionId,
                                              @Param("currentDate") LocalDate currentDate);





    @Query("SELECT COUNT(ae) FROM AffectationEmploye ae WHERE ae.equipe.id = :equipeId AND ae.mission.id = :missionId AND (ae.dateFin IS NULL OR ae.dateFin >= :currentDate)")
    long countByEquipeIdAndMissionIdAndDateFinAfterOrDateFinNull(
            @Param("equipeId") Long equipeId,
            @Param("missionId") Long missionId,
            @Param("currentDate") LocalDate currentDate);

    Optional<AffectationEmploye> findByEmployeAndMission(Employe employe, Mission mission);

}

