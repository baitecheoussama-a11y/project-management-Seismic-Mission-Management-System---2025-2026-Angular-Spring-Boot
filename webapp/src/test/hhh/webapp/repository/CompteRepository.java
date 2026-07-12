package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {

    Optional<Compte> findByUsername(String username);

    Optional<Compte> findByEmployeId(Long employeId);

    // ✅ إصلاح: استخدم StatusCompte.ACTIVE مباشرة
    @Query("SELECT COUNT(c) FROM Compte c WHERE c.status = 'ACTIVE'")
    long countByStatus();

    // بديل: استخدم enum مباشرة
    long countByStatus(com.pfe.webapp.entity.StatusCompte status);
}