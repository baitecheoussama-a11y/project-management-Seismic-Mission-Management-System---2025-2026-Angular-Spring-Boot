package com.pfe.webapp.service;

import com.pfe.webapp.dto.event.TypeEvenementDTO;
import com.pfe.webapp.dto.event.TypeEvenementRequestDTO;
import java.util.List;

public interface TypeEvenementService {

    TypeEvenementDTO createTypeEvenement(TypeEvenementRequestDTO requestDTO);

    TypeEvenementDTO updateTypeEvenement(Long id, TypeEvenementRequestDTO requestDTO);

    void deleteTypeEvenement(Long id);

    TypeEvenementDTO getTypeEvenementById(Long id);

    List<TypeEvenementDTO> getAllTypeEvenements();

    List<TypeEvenementDTO> getActiveTypeEvenements();

    List<TypeEvenementDTO> getInactiveTypeEvenements();

    List<TypeEvenementDTO> searchTypeEvenements(String keyword);

    TypeEvenementDTO toggleTypeEvenementStatus(Long id);

    boolean existsByNom(String nom);
    // Add to TypeEvenementService.java
    TypeEvenementDTO updateStatus(Long id, Boolean actif);
}