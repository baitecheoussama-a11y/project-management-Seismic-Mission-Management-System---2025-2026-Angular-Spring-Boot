package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {

    Optional<Employe> findByEmail(String email);

    Optional<Employe> findByNumIdentite(String numIdentite);

    // ✅ إصلاح: استخدم createdAt بدلاً من dateCreation
    @Query("SELECT e.id, e.nom, e.prenom, e.email, e.createdAt FROM Employe e ORDER BY e.id DESC")
    List<Object[]> findTop5ByOrderByIdDesc();

    // ✅ إصلاح: استخدم createdAt بدلاً من dateCreation
    @Query("SELECT e.sexe, COUNT(e) FROM Employe e GROUP BY e.sexe")
    List<Object[]> countBySexe();

    // بديل: إذا أردت تحديد الـ LIMIT 5
    @Query(value = "SELECT id, nom, prenom, email, created_at FROM employe ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findRecentEmployees();

    // EmployeRepository.java - Add this method
    List<Employe> findByFonctionId(Long fonctionId);
}