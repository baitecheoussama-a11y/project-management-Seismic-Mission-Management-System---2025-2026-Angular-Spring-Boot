package com.pfe.webapp.repository.medical;

import com.pfe.webapp.entity.AntecedentsMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AntecedentsMedicalRepository extends JpaRepository<AntecedentsMedical, Long> {
    List<AntecedentsMedical> findByEtatMedicalId(Long etatMedicalId);
    void deleteByEtatMedicalId(Long etatMedicalId);
}