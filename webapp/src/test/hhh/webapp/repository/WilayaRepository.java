// repository/WilayaRepository.java
package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Wilaya;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WilayaRepository extends JpaRepository<Wilaya, Integer> {
    Optional<Wilaya> findByNom(String nom);
    boolean existsByNumWilaya(Integer numWilaya);
}