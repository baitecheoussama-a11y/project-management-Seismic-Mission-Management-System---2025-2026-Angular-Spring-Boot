package com.pfe.webapp.service;

import com.pfe.webapp.dto.incident.IncidentDTO;
import com.pfe.webapp.dto.incident.IncidentRequestDTO;
import com.pfe.webapp.dto.incident.IncidentResponseDTO;
import com.pfe.webapp.dto.incident.CoordonneeIncidentRequestDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.EmployeRepository;
import com.pfe.webapp.repository.medical.EtatMedicalRepository;
import com.pfe.webapp.repository.CoordonneeRepository;
import com.pfe.webapp.repository.incident.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class IncidentServiceImpl implements IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private EtatMedicalRepository etatMedicalRepository;

    @Autowired
    private CoordonneeRepository coordonneeRepository;

    // ========== CONVERTER METHODS ==========

    private IncidentDTO convertToDTO(Incident incident) {
        if (incident == null) return null;

        IncidentDTO dto = new IncidentDTO();
        dto.setId(incident.getId());
        dto.setDescription(incident.getDescription());
        dto.setDateIncident(incident.getDateIncident());

        // Type
        if (incident.getType() != null) {
            dto.setType(incident.getType().name());
            dto.setTypeLabel(incident.getType().getLabel());
            dto.setTypeColor(incident.getType().getColor());
        }

        // Niveau Gravite
        if (incident.getNiveauGravite() != null) {
            dto.setNiveauGravite(incident.getNiveauGravite().name());
            dto.setNiveauGraviteLabel(incident.getNiveauGravite().getLabel());
            dto.setNiveauGraviteColor(incident.getNiveauGravite().getColor());
        }

        // Employe
        if (incident.getEmploye() != null) {
            dto.setEmployeId(incident.getEmploye().getId());
            dto.setEmployeNom(incident.getEmploye().getNom());
            dto.setEmployePrenom(incident.getEmploye().getPrenom());
            dto.setEmployeEmail(incident.getEmploye().getEmail());
        }

        // Etat Medical
        if (incident.getEtatMedical() != null) {
            dto.setEtatMedicalId(incident.getEtatMedical().getId());
            dto.setGroupeSanguin(incident.getEtatMedical().getGroupeSanguin());
        }

        // Coordonnee
        if (incident.getCoordonnee() != null) {
            Coordonnee coord = incident.getCoordonnee();
            dto.setCoordonneeId(coord.getId());
            dto.setLatitude(coord.getLatitude());
            dto.setLongitude(coord.getLongitude());
            dto.setOrdre(coord.getOrdre());

            if (coord.getSite() != null) {
                dto.setSiteId(coord.getSite().getId());
                dto.setSiteSurface(coord.getSite().getSurface());
                if (coord.getSite().getWilaya() != null) {
                    dto.setWilayaNum(coord.getSite().getWilaya().getNumWilaya());
                    dto.setWilayaNom(coord.getSite().getWilaya().getNom());
                }
            }
        }

        return dto;
    }

    private Coordonnee createAndSaveCoordonnee(CoordonneeIncidentRequestDTO dto) {
        if (dto == null) return null;

        // Check if coordinates are provided (non-empty)
        boolean hasLatitude = dto.getLatitude() != null && dto.getLatitude() != 0;
        boolean hasLongitude = dto.getLongitude() != null && dto.getLongitude() != 0;

        if (!hasLatitude && !hasLongitude) {
            return null;  // No coordinates provided, skip
        }

        Coordonnee coordonnee = new Coordonnee();
        coordonnee.setLatitude(dto.getLatitude());
        coordonnee.setLongitude(dto.getLongitude());
        coordonnee.setOrdre(dto.getOrdre() != null ? dto.getOrdre() : 1);

        // Save Coordonnee FIRST before linking to Incident
        return coordonneeRepository.save(coordonnee);
    }

    // ========== CREATE INCIDENT ==========

    @Override
    public IncidentResponseDTO createIncident(IncidentRequestDTO requestDTO) {
        // Validate required fields
        if (requestDTO.getEmployeId() == null) {
            throw new RuntimeException("Employee ID is required");
        }

        if (requestDTO.getType() == null || requestDTO.getType().isEmpty()) {
            throw new RuntimeException("Incident type is required");
        }

        if (requestDTO.getNiveauGravite() == null || requestDTO.getNiveauGravite().isEmpty()) {
            throw new RuntimeException("Severity level is required");
        }

        if (requestDTO.getDateIncident() == null) {
            throw new RuntimeException("Incident date is required");
        }

        if (requestDTO.getDescription() == null || requestDTO.getDescription().isEmpty()) {
            throw new RuntimeException("Description is required");
        }

        // Get Employe
        Employe employe = employeRepository.findById(requestDTO.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employe not found with id: " + requestDTO.getEmployeId()));

        // Get EtatMedical (optional - may not exist)
        EtatMedical etatMedical = null;
        try {
            etatMedical = etatMedicalRepository.findByEmployeId(employe.getId()).orElse(null);
        } catch (Exception e) {
            // EtatMedical may not exist, that's fine
        }

        // Create and SAVE Coordonnee FIRST (if provided)
        Coordonnee coordonnee = null;
        if (requestDTO.getCoordonnee() != null) {
            coordonnee = createAndSaveCoordonnee(requestDTO.getCoordonnee());
        }

        // Create Incident
        Incident incident = new Incident();
        incident.setDescription(requestDTO.getDescription());
        incident.setDateIncident(requestDTO.getDateIncident());
        incident.setEmploye(employe);
        incident.setEtatMedical(etatMedical);
        incident.setCoordonnee(coordonnee);  // Now coordonnee is already saved or null

        // Set Type
        try {
            incident.setType(TypeIncident.valueOf(requestDTO.getType()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid incident type: " + requestDTO.getType());
        }

        // Set Niveau Gravite
        try {
            incident.setNiveauGravite(NiveauGravite.valueOf(requestDTO.getNiveauGravite()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid severity level: " + requestDTO.getNiveauGravite());
        }

        // Save Incident
        Incident savedIncident = incidentRepository.save(incident);

        return new IncidentResponseDTO(convertToDTO(savedIncident));
    }

    // ========== UPDATE INCIDENT ==========

    @Override
    public IncidentResponseDTO updateIncident(Long id, IncidentRequestDTO requestDTO) {
        Incident existingIncident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));

        // Get Employe
        Employe employe = employeRepository.findById(requestDTO.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employe not found with id: " + requestDTO.getEmployeId()));

        // Get EtatMedical
        EtatMedical etatMedical = null;
        try {
            etatMedical = etatMedicalRepository.findByEmployeId(employe.getId()).orElse(null);
        } catch (Exception e) {
            // EtatMedical may not exist, that's fine
        }

        // Handle Coordonnee
        Coordonnee coordonnee = existingIncident.getCoordonnee();
        boolean hasCoordinates = requestDTO.getCoordonnee() != null &&
                ((requestDTO.getCoordonnee().getLatitude() != null && requestDTO.getCoordonnee().getLatitude() != 0) ||
                        (requestDTO.getCoordonnee().getLongitude() != null && requestDTO.getCoordonnee().getLongitude() != 0));

        if (hasCoordinates) {
            if (coordonnee == null) {
                // Create new coordonnee
                coordonnee = createAndSaveCoordonnee(requestDTO.getCoordonnee());
            } else {
                // Update existing coordonnee
                coordonnee.setLatitude(requestDTO.getCoordonnee().getLatitude());
                coordonnee.setLongitude(requestDTO.getCoordonnee().getLongitude());
                coordonnee.setOrdre(requestDTO.getCoordonnee().getOrdre() != null ? requestDTO.getCoordonnee().getOrdre() : 1);
                coordonnee = coordonneeRepository.save(coordonnee);
            }
        } else if (coordonnee != null && requestDTO.getCoordonnee() == null) {
            // Coordinates were removed, delete the existing coordonnee
            coordonneeRepository.delete(coordonnee);
            coordonnee = null;
        }

        // Update Incident fields
        if (requestDTO.getDescription() != null) {
            existingIncident.setDescription(requestDTO.getDescription());
        }

        if (requestDTO.getDateIncident() != null) {
            existingIncident.setDateIncident(requestDTO.getDateIncident());
        }

        existingIncident.setEmploye(employe);
        existingIncident.setEtatMedical(etatMedical);
        existingIncident.setCoordonnee(coordonnee);

        // Set Type
        if (requestDTO.getType() != null && !requestDTO.getType().isEmpty()) {
            try {
                existingIncident.setType(TypeIncident.valueOf(requestDTO.getType()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid incident type: " + requestDTO.getType());
            }
        }

        // Set Niveau Gravite
        if (requestDTO.getNiveauGravite() != null && !requestDTO.getNiveauGravite().isEmpty()) {
            try {
                existingIncident.setNiveauGravite(NiveauGravite.valueOf(requestDTO.getNiveauGravite()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid severity level: " + requestDTO.getNiveauGravite());
            }
        }

        Incident updatedIncident = incidentRepository.save(existingIncident);

        return new IncidentResponseDTO(convertToDTO(updatedIncident));
    }

    // ========== DELETE INCIDENT ==========

    @Override
    public void deleteIncident(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));

        // Delete associated coordonnee first if exists
        if (incident.getCoordonnee() != null) {
            coordonneeRepository.delete(incident.getCoordonnee());
        }

        incidentRepository.delete(incident);
    }

    // ========== GET METHODS ==========

    @Override
    public IncidentResponseDTO getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));
        return new IncidentResponseDTO(convertToDTO(incident));
    }

    @Override
    public List<IncidentResponseDTO> getAllIncidents() {
        return incidentRepository.findAll().stream()
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)))
                .collect(Collectors.toList());
    }

    @Override
    public Page<IncidentResponseDTO> getAllIncidents(Pageable pageable) {
        return incidentRepository.findAll(pageable)
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)));
    }

    @Override
    public List<IncidentResponseDTO> getIncidentsByEmploye(Long employeId) {
        return incidentRepository.findByEmployeId(employeId).stream()
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)))
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentResponseDTO> getIncidentsByType(String type) {
        return incidentRepository.findByType(type).stream()
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)))
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentResponseDTO> getIncidentsByGravite(String niveauGravite) {
        return incidentRepository.findByNiveauGravite(niveauGravite).stream()
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)))
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentResponseDTO> getIncidentsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new RuntimeException("Start date and end date are required");
        }
        return incidentRepository.findByDateIncidentBetween(startDate, endDate).stream()
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)))
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentResponseDTO> getIncidentsByDate(LocalDate date) {
        if (date == null) {
            throw new RuntimeException("Date is required");
        }
        return incidentRepository.findByDateIncident(date).stream()
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)))
                .collect(Collectors.toList());
    }

    @Override
    public List<IncidentResponseDTO> getRecentIncidents() {
        return incidentRepository.findRecentIncidents(LocalDate.now().minusMonths(3)).stream()
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)))
                .collect(Collectors.toList());
    }

    @Override
    public Page<IncidentResponseDTO> searchIncidents(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllIncidents(pageable);
        }
        return incidentRepository.searchByKeyword(keyword.trim(), pageable)
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)));
    }

    @Override
    public long getIncidentsCount() {
        return incidentRepository.count();
    }

    @Override
    public Map<String, Long> getIncidentsCountByType() {
        List<Object[]> results = incidentRepository.countByType();
        Map<String, Long> countMap = new HashMap<>();
        for (Object[] result : results) {
            TypeIncident type = (TypeIncident) result[0];
            Long count = (Long) result[1];
            if (type != null) {
                countMap.put(type.getLabel(), count);
            } else {
                countMap.put("Unknown", count);
            }
        }
        return countMap;
    }

    @Override
    public Map<String, Long> getIncidentsCountByGravite() {
        List<Object[]> results = incidentRepository.countByNiveauGravite();
        Map<String, Long> countMap = new HashMap<>();
        for (Object[] result : results) {
            NiveauGravite gravite = (NiveauGravite) result[0];
            Long count = (Long) result[1];
            if (gravite != null) {
                countMap.put(gravite.getLabel(), count);
            } else {
                countMap.put("Unknown", count);
            }
        }
        return countMap;
    }

    // IncidentServiceImpl.java - أضف هذه التنفيذات

    @Override
    public List<IncidentResponseDTO> getIncidentsByMission(Long missionId) {
        return incidentRepository.findIncidentsByMissionId(missionId).stream()
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)))
                .collect(Collectors.toList());
    }

    @Override
    public Page<IncidentResponseDTO> getIncidentsByMission(Long missionId, Pageable pageable) {
        return incidentRepository.findIncidentsByMissionId(missionId, pageable)
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)));
    }

    @Override
    public Page<IncidentResponseDTO> searchIncidentsByMission(Long missionId, String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getIncidentsByMission(missionId, pageable);
        }
        return incidentRepository.searchIncidentsByMissionId(missionId, keyword.trim(), pageable)
                .map(incident -> new IncidentResponseDTO(convertToDTO(incident)));
    }

    @Override
    public Map<String, Long> getIncidentsStatsByMission(Long missionId) {
        List<Incident> incidents = incidentRepository.findIncidentsByMissionId(missionId);
        Map<String, Long> statsByGravite = new HashMap<>();

        for (Incident incident : incidents) {
            if (incident.getNiveauGravite() != null) {
                String label = incident.getNiveauGravite().getLabel();
                statsByGravite.put(label, statsByGravite.getOrDefault(label, 0L) + 1);
            }
        }
        return statsByGravite;
    }
}