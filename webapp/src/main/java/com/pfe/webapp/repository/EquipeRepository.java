// repository/EquipeRepository.java
package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Equipe;
import com.pfe.webapp.entity.TypeActivite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    List<Equipe> findByType(TypeActivite type);

    Optional<Equipe> findByNom(String nom);

    List<Equipe> findByType(String type);

    @Query("SELECT DISTINCT e FROM Equipe e LEFT JOIN FETCH e.affectations WHERE e.id = :id")
    Optional<Equipe> findByIdWithAffectations(@Param("id") Long id);
}