    // repository/RapportRepository.java
    package com.pfe.webapp.repository.rapport;

    import com.pfe.webapp.entity.Rapport;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;

    @Repository
    public interface RapportRepository extends JpaRepository<Rapport, Long> {

        @Query("SELECT DISTINCT r FROM Rapport r JOIN r.affectationEquipes ae WHERE ae.equipe.id = :equipeId")
        List<Rapport> findRapportsByEquipeId(@Param("equipeId") Long equipeId);



        @Modifying
        @Transactional
        @Query("DELETE FROM Rapport r WHERE r.project.id = :projectId")
        void deleteByProjectId(@Param("projectId") Long projectId);

        List<Rapport> findByProjectId(Long projectId);

        List<Rapport> findByProjectMissionId(Long missionId);

        @Query("SELECT r FROM Rapport r WHERE r.project.mission.id = :missionId ORDER BY r.date DESC")
        List<Rapport> findByMissionIdOrderByDateDesc(@Param("missionId") Long missionId);

        @Query("SELECT r FROM Rapport r WHERE r.project.id = :projectId ORDER BY r.date DESC")
        List<Rapport> findByProjectIdOrderByDateDesc(@Param("projectId") Long projectId);

        @Query("SELECT r FROM Rapport r WHERE r.project.mission.id = :missionId AND r.titre LIKE %:keyword%")
        List<Rapport> searchByKeyword(@Param("missionId") Long missionId, @Param("keyword") String keyword);

        @Query("SELECT r FROM Rapport r WHERE r.titre LIKE %:keyword% OR r.resume LIKE %:keyword%")
        List<Rapport> searchByKeyword(@Param("keyword") String keyword);

        @Query("SELECT r FROM Rapport r WHERE (r.titre LIKE %:keyword% OR r.resume LIKE %:keyword%) AND r.project.id = :projectId")
        List<Rapport> searchByKeywordAndProject(@Param("keyword") String keyword, @Param("projectId") Long projectId);

    }