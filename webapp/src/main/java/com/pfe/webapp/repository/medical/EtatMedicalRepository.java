package com.pfe.webapp.repository.medical;

import com.pfe.webapp.entity.EtatMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EtatMedicalRepository extends JpaRepository<EtatMedical, Long> {
    Optional<EtatMedical> findByEmployeId(Long employeId);
    boolean existsByEmployeId(Long employeId);
}