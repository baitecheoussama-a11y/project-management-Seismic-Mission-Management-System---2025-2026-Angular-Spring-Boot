// MaterielRepository.java
package com.pfe.webapp.repository;

import com.pfe.webapp.entity.materiel.Materiel;
import com.pfe.webapp.entity.StatusMateriel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.List;


public interface MaterielRepository extends JpaRepository<Materiel, Long> {
    List<Materiel> findByStatus(StatusMateriel status);
    List<Materiel> findByTypeMaterielIdTypeMateriel(Long typeId);
    List<Materiel> findByCodeMaterielContaining(String code);
    List<Materiel> findByTypeMateriel_Categorie_IdCategorie(Long categorieId);
    List<Materiel> findByTypeMateriel_IdTypeMateriel(Long typeId);

    // إضافة هذه الدوال في MaterielRepository.java

    long countByTypeMateriel_Categorie_IdCategorie(Long categoryId);
    long countByTypeMateriel_IdTypeMateriel(Long typeId);



    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM AffectationMateriel a " +
            "WHERE a.materiel.idMateriel = :materielId " +
            "AND (a.dateFin IS NULL OR a.dateFin >= CURRENT_DATE)")
    boolean isMaterielInUse(@Param("materielId") Long materielId);

}