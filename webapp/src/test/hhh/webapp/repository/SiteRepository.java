// repository/SiteRepository.java
package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    @Query("SELECT s FROM Site s LEFT JOIN FETCH s.coordonnees WHERE s.project.id = :projectId")
    Optional<Site> findByProjectId(@Param("projectId") Long projectId);

    boolean existsByProjectId(Long projectId);
}