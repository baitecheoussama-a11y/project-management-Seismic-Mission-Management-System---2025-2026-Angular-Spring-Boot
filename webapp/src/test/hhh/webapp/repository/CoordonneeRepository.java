// repository/CoordonneeRepository.java
package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Coordonnee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CoordonneeRepository extends JpaRepository<Coordonnee, Long> {

    List<Coordonnee> findBySiteId(Long siteId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Coordonnee c WHERE c.site.id = :siteId")
    void deleteBySiteId(@Param("siteId") Long siteId);
}