package com.pfe.webapp.repository.ressource;

import com.pfe.webapp.entity.ressource.TypeRessource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TypeRessourceRepository extends JpaRepository<TypeRessource, Long> {
    List<TypeRessource> findByCategorieRessourceIdCategorieRessource(Long categorieId);
}