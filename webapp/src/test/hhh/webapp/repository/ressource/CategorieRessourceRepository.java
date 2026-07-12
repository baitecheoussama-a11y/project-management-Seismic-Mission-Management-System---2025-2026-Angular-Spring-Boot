package com.pfe.webapp.repository.ressource;

import com.pfe.webapp.entity.ressource.CategorieRessource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorieRessourceRepository extends JpaRepository<CategorieRessource, Long> {
}