// MaterielImageRepository.java - CORRECTED
package com.pfe.webapp.repository;

import com.pfe.webapp.entity.materiel.MaterielImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaterielImageRepository extends JpaRepository<MaterielImage, Long> {
    // Fixed: Use correct property path
    List<MaterielImage> findByMaterielIdMateriel(Long materielId);
    void deleteByMaterielIdMateriel(Long materielId);
}