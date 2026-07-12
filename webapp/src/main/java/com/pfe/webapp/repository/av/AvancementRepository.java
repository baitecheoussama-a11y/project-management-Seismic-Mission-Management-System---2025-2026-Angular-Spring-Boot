// repository/AvancementRepository.java
package com.pfe.webapp.repository.av;

import com.pfe.webapp.entity.Avancement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AvancementRepository extends JpaRepository<Avancement, Long> {

    List<Avancement> findByEtatAvancementId(Long etatAvancementId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Avancement a WHERE a.etatAvancement.id = :etatAvancementId")
    void deleteByEtatAvancementId(@Param("etatAvancementId") Long etatAvancementId);
}