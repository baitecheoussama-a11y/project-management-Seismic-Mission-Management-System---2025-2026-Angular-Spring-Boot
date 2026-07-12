package com.pfe.webapp.repository.event;

import com.pfe.webapp.entity.TypeEvenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TypeEvenementRepository extends JpaRepository<TypeEvenement, Long> {

    Optional<TypeEvenement> findByNom(String nom);

    List<TypeEvenement> findByActifTrue();

    List<TypeEvenement> findByActifFalse();

    boolean existsByNom(String nom);

    // ✅ أضف هذه الدالة للبحث
    @Query("SELECT t FROM TypeEvenement t WHERE LOWER(t.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TypeEvenement> searchByName(@Param("keyword") String keyword);

    // ✅ أضف هذه الدالة للترتيب
    List<TypeEvenement> findAllByOrderByNomAsc();

    
}