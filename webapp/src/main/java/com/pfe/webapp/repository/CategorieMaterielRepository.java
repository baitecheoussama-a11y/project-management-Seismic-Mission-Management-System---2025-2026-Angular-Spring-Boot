package com.pfe.webapp.repository;

import com.pfe.webapp.entity.materiel.CategorieMateriel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CategorieMaterielRepository extends JpaRepository<CategorieMateriel, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM TypeMateriel t WHERE t.categorie.idCategorie = :categoryId")
    void deleteTypesByCategoryId(@Param("categoryId") Long categoryId);
}