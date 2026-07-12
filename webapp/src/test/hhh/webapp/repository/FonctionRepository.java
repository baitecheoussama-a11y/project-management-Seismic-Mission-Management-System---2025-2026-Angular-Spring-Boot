package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Fonction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FonctionRepository extends JpaRepository<Fonction, Long> {
    Optional<Fonction> findByNom(String nom);

    boolean existsByNom(String nom);

    @Query("SELECT f FROM Fonction f LEFT JOIN FETCH f.employes WHERE f.id = :id")
    Optional<Fonction> findByIdWithEmployes(@Param("id") Long id);

    @Query("SELECT f FROM Fonction f")
    List<Fonction> findAllWithEmployesCount();
}