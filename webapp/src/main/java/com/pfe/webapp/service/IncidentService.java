package com.pfe.webapp.service;

import com.pfe.webapp.dto.incident.IncidentDTO;
import com.pfe.webapp.dto.incident.IncidentRequestDTO;
import com.pfe.webapp.dto.incident.IncidentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IncidentService {

    IncidentResponseDTO createIncident(IncidentRequestDTO requestDTO);

    IncidentResponseDTO updateIncident(Long id, IncidentRequestDTO requestDTO);

    void deleteIncident(Long id);

    IncidentResponseDTO getIncidentById(Long id);

    List<IncidentResponseDTO> getAllIncidents();

    Page<IncidentResponseDTO> getAllIncidents(Pageable pageable);

    List<IncidentResponseDTO> getIncidentsByEmploye(Long employeId);

    List<IncidentResponseDTO> getIncidentsByType(String type);

    List<IncidentResponseDTO> getIncidentsByGravite(String niveauGravite);

    List<IncidentResponseDTO> getIncidentsByDateRange(LocalDate startDate, LocalDate endDate);

    List<IncidentResponseDTO> getIncidentsByDate(LocalDate date);

    List<IncidentResponseDTO> getRecentIncidents();

    Page<IncidentResponseDTO> searchIncidents(String keyword, Pageable pageable);

    long getIncidentsCount();

    Map<String, Long> getIncidentsCountByType();

    Map<String, Long> getIncidentsCountByGravite();
    // IncidentService.java - أضف هذه الدوال
    List<IncidentResponseDTO> getIncidentsByMission(Long missionId);
    Page<IncidentResponseDTO> getIncidentsByMission(Long missionId, Pageable pageable);
    Page<IncidentResponseDTO> searchIncidentsByMission(Long missionId, String keyword, Pageable pageable);
    Map<String, Long> getIncidentsStatsByMission(Long missionId);
}