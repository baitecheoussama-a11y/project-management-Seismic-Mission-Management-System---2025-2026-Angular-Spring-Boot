package com.pfe.webapp.repository.team;

import com.pfe.webapp.entity.Active;
import com.pfe.webapp.entity.AffectationEquipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AffectationEquipeRepository extends JpaRepository<AffectationEquipe, Long> {

    // Returns List because one active can have multiple team assignments
    @Query("SELECT ae FROM AffectationEquipe ae WHERE ae.active.id = :activeId")
    List<AffectationEquipe> findByActiveId(@Param("activeId") Long activeId);

    // Returns Optional for unique combination (one team + one active + one mission)
    @Query("SELECT ae FROM AffectationEquipe ae WHERE ae.equipe.id = :equipeId AND ae.active.id = :activeId AND ae.mission.id = :missionId")
    Optional<AffectationEquipe> findByEquipeIdAndActiveIdAndMissionId(@Param("equipeId") Long equipeId,
                                                                      @Param("activeId") Long activeId,
                                                                      @Param("missionId") Long missionId);



    // Returns List for all assignments of an equipe
    @Query("SELECT ae FROM AffectationEquipe ae WHERE ae.equipe.id = :equipeId")
    List<AffectationEquipe> findByEquipeId(@Param("equipeId") Long equipeId);

    // Returns List for all assignments of a mission
    @Query("SELECT ae FROM AffectationEquipe ae WHERE ae.mission.id = :missionId")
    List<AffectationEquipe> findByMissionId(@Param("missionId") Long missionId);

    // Returns List for all assignments of an active with details
    @Query("SELECT ae FROM AffectationEquipe ae LEFT JOIN FETCH ae.active LEFT JOIN FETCH ae.mission WHERE ae.active.id = :activeId")
    List<AffectationEquipe> findAssignmentsByActiveIdWithDetails(@Param("activeId") Long activeId);

    // Returns List for all assignments of an equipe in a mission with details
    @Query("SELECT ae FROM AffectationEquipe ae LEFT JOIN FETCH ae.active LEFT JOIN FETCH ae.mission WHERE ae.equipe.id = :equipeId AND ae.mission.id = :missionId")
    List<AffectationEquipe> findAllByEquipeIdAndMissionIdWithDetails(@Param("equipeId") Long equipeId,
                                                                     @Param("missionId") Long missionId);

    // Returns List for all assignments of an equipe in a mission
    @Query("SELECT ae FROM AffectationEquipe ae WHERE ae.equipe.id = :equipeId AND ae.mission.id = :missionId")
    List<AffectationEquipe> findAllByEquipeIdAndMissionId(@Param("equipeId") Long equipeId,
                                                          @Param("missionId") Long missionId);

    // Count assignments by equipe ID
    @Query("SELECT COUNT(ae) FROM AffectationEquipe ae WHERE ae.equipe.id = :equipeId")
    long countByEquipeId(@Param("equipeId") Long equipeId);

    // Count assignments by equipe ID and mission ID
    @Query("SELECT COUNT(ae) FROM AffectationEquipe ae WHERE ae.equipe.id = :equipeId AND ae.mission.id = :missionId")
    long countByEquipeIdAndMissionId(@Param("equipeId") Long equipeId,
                                     @Param("missionId") Long missionId);

    // Delete by equipe, active, and mission
    @Modifying
    @Query("DELETE FROM AffectationEquipe ae WHERE ae.equipe.id = :equipeId AND ae.active.id = :activeId AND ae.mission.id = :missionId")
    void deleteByEquipeIdAndActiveIdAndMissionId(@Param("equipeId") Long equipeId,
                                                 @Param("activeId") Long activeId,
                                                 @Param("missionId") Long missionId);

    // Check if assignment exists
    boolean existsByEquipeIdAndActiveIdAndMissionId(Long equipeId, Long activeId, Long missionId);

    // Check if active with same code exists in the same mission
    @Query("SELECT COUNT(ae) > 0 FROM AffectationEquipe ae WHERE ae.active.codeActive = :codeActive AND ae.mission.id = :missionId")
    boolean existsByActiveCodeAndMissionId(@Param("codeActive") String codeActive,
                                           @Param("missionId") Long missionId);

    // Get all actives assigned to a mission
    @Query("SELECT DISTINCT ae.active FROM AffectationEquipe ae WHERE ae.mission.id = :missionId")
    List<Active> findActivesByMissionId(@Param("missionId") Long missionId);




    // ✅ FIXED: Return List, find by equipe and mission
    @Query("SELECT ae FROM AffectationEquipe ae WHERE ae.equipe.id = :equipeId AND ae.mission.id = :missionId")
    List<AffectationEquipe> findByEquipeIdAndMissionId(@Param("equipeId") Long equipeId, @Param("missionId") Long missionId);

    // ✅ NEW: Find by equipe, mission, AND active
    @Query("SELECT ae FROM AffectationEquipe ae WHERE ae.equipe.id = :equipeId AND ae.mission.id = :missionId AND ae.active.id = :activeId")
    List<AffectationEquipe> findByEquipeIdAndMissionIdAndActiveId(@Param("equipeId") Long equipeId,
                                                                  @Param("missionId") Long missionId,
                                                                  @Param("activeId") Long activeId);

    // ✅ Find by equipe and mission (returns Optional if you expect only one)
    Optional<AffectationEquipe> findByEquipeIdAndMissionIdAndActiveIsNull(Long equipeId, Long missionId);


}