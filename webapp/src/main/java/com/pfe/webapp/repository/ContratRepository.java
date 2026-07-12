package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {

    List<Contrat> findByEmployeId(Long employeId);

    // ✅ إصلاح: استخدم type كـ String لأن enum
    @Query("SELECT c.type, COUNT(c) FROM Contrat c GROUP BY c.type")
    List<Object[]> countByType();
}