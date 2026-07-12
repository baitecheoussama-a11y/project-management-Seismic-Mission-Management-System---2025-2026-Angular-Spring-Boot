// TypeMaterielRepository.java
package com.pfe.webapp.repository;

import com.pfe.webapp.entity.materiel.TypeMateriel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TypeMaterielRepository extends JpaRepository<TypeMateriel, Long> {
    List<TypeMateriel> findByCategorieIdCategorie(Long categorieId);
}