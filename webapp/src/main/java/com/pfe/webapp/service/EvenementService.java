package com.pfe.webapp.service;

import com.pfe.webapp.dto.event.EvenementRequestDTO;
import com.pfe.webapp.dto.event.EvenementResponseDTO;
import java.time.LocalDate;
import java.util.List;

public interface EvenementService {

    // Create
    EvenementResponseDTO createEvenement(EvenementRequestDTO requestDTO);

    // Update
    EvenementResponseDTO updateEvenement(Long id, EvenementRequestDTO requestDTO);

    // Delete
    void deleteEvenement(Long id);

    // Read - Single
    EvenementResponseDTO getEvenementById(Long id);

    // Read - All
    List<EvenementResponseDTO> getAllEvenements();

    // Read - By Mission
    List<EvenementResponseDTO> getEvenementsByMission(Long missionId);

    // Read - By Type
    List<EvenementResponseDTO> getEvenementsByType(Long typeEvenementId);

    // Read - By Date
    List<EvenementResponseDTO> getEvenementsByDate(LocalDate date);

    // Read - By Date Range
    List<EvenementResponseDTO> getEvenementsByDateRange(LocalDate startDate, LocalDate endDate);

    // Read - Upcoming
    List<EvenementResponseDTO> getUpcomingEvenements();

    // Read - Past
    List<EvenementResponseDTO> getPastEvenements();

    // Read - Today's Events
    List<EvenementResponseDTO> getTodaysEvenements();

    // Search
    List<EvenementResponseDTO> searchEvenements(String keyword);

    // Count
    long getTotalEvenementsCount();
}