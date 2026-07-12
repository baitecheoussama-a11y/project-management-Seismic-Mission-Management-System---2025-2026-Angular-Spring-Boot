// src/main/java/com/pfe/webapp/repository/FichierRepository.java
package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Fichier;
import com.pfe.webapp.entity.TypeFichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FichierRepository extends JpaRepository<Fichier, Long> {

    List<Fichier> findByRapportId(Long rapportId);

    List<Fichier> findByRapportIdAndType(Long rapportId, TypeFichier type);

    @Query("SELECT f FROM Fichier f WHERE f.rapport.project.id = :projectId")
    List<Fichier> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(f) FROM Fichier f WHERE f.rapport.id = :rapportId")
    long countByRapportId(@Param("rapportId") Long rapportId);

    @Query("SELECT f.type, COUNT(f) FROM Fichier f WHERE f.rapport.id = :rapportId GROUP BY f.type")
    List<Object[]> countByTypeForRapport(@Param("rapportId") Long rapportId);

    void deleteByRapportId(Long rapportId);
}