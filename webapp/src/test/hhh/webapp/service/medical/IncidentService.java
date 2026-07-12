package com.pfe.webapp.service.medical;

import com.pfe.webapp.dto.IncidentDTO;
import com.pfe.webapp.dto.IncidentResponseDTO;
import com.pfe.webapp.dto.CoordonneeDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.*;
import com.pfe.webapp.repository.incident.IncidentRepository;
import com.pfe.webapp.repository.medical.EtatMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private EtatMedicalRepository etatMedicalRepository;

    @Autowired
    private CoordonneeRepository coordonneeRepository;

    @Transactional
    public IncidentResponseDTO createIncident(IncidentDTO dto) {
        Incident incident = new Incident();
        incident.setType(dto.getType());
        incident.setDescription(dto.getDescription());
        incident.setDateIncident(dto.getDateIncident());
        incident.setNiveauGravite(dto.getNiveauGravite());

        if (dto.getEmployeId() != null) {
            Employe employe = employeRepository.findById(dto.getEmployeId())
                    .orElseThrow(() -> new RuntimeException("Employe not found with id: " + dto.getEmployeId()));
            incident.setEmploye(employe);
        }

        if (dto.getEtatMedicalId() != null) {
            EtatMedical etatMedical = etatMedicalRepository.findById(dto.getEtatMedicalId())
                    .orElseThrow(() -> new RuntimeException("EtatMedical not found with id: " + dto.getEtatMedicalId()));
            incident.setEtatMedical(etatMedical);
        }

        if (dto.getCoordonneeId() != null) {
            Coordonnee coordonnee = coordonneeRepository.findById(dto.getCoordonneeId())
                    .orElseThrow(() -> new RuntimeException("Coordonnee not found with id: " + dto.getCoordonneeId()));
            incident.setCoordonnee(coordonnee);
        }

        incident = incidentRepository.save(incident);
        return convertToResponseDTO(incident);
    }

    @Transactional
    public IncidentResponseDTO updateIncident(Long id, IncidentDTO dto) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));

        incident.setType(dto.getType());
        incident.setDescription(dto.getDescription());
        incident.setDateIncident(dto.getDateIncident());
        incident.setNiveauGravite(dto.getNiveauGravite());

        if (dto.getEmployeId() != null) {
            Employe employe = employeRepository.findById(dto.getEmployeId())
                    .orElseThrow(() -> new RuntimeException("Employe not found with id: " + dto.getEmployeId()));
            incident.setEmploye(employe);
        }

        if (dto.getEtatMedicalId() != null) {
            EtatMedical etatMedical = etatMedicalRepository.findById(dto.getEtatMedicalId())
                    .orElseThrow(() -> new RuntimeException("EtatMedical not found with id: " + dto.getEtatMedicalId()));
            incident.setEtatMedical(etatMedical);
        }

        if (dto.getCoordonneeId() != null) {
            Coordonnee coordonnee = coordonneeRepository.findById(dto.getCoordonneeId())
                    .orElseThrow(() -> new RuntimeException("Coordonnee not found with id: " + dto.getCoordonneeId()));
            incident.setCoordonnee(coordonnee);
        }

        incident = incidentRepository.save(incident);
        return convertToResponseDTO(incident);
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> getAllIncidents() {
        return incidentRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public IncidentResponseDTO getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident not found with id: " + id));
        return convertToResponseDTO(incident);
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> getIncidentsByEmployeId(Long employeId) {
        return incidentRepository.findByEmployeId(employeId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> getIncidentsByEtatMedicalId(Long etatMedicalId) {
        return incidentRepository.findByEtatMedicalId(etatMedicalId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> getIncidentsByType(TypeIncident type) {
        return incidentRepository.findByType(type).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> getIncidentsByNiveauGravite(NiveauGravite niveauGravite) {
        return incidentRepository.findByNiveauGravite(niveauGravite).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IncidentResponseDTO> getIncidentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return incidentRepository.findByDateIncidentBetween(startDate, endDate).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteIncident(Long id) {
        incidentRepository.deleteById(id);
    }

    private IncidentResponseDTO convertToResponseDTO(Incident incident) {
        IncidentResponseDTO response = new IncidentResponseDTO();
        response.setId(incident.getId());
        response.setType(incident.getType());
        response.setDescription(incident.getDescription());
        response.setDateIncident(incident.getDateIncident());
        response.setNiveauGravite(incident.getNiveauGravite());

        if (incident.getEmploye() != null) {
            response.setEmployeId(incident.getEmploye().getId());
            response.setEmployeNomComplet(incident.getEmploye().getPrenom() + " " + incident.getEmploye().getNom());
        }

        if (incident.getEtatMedical() != null) {
            response.setEtatMedicalId(incident.getEtatMedical().getId());
        }

        if (incident.getCoordonnee() != null) {
            CoordonneeDTO coordonneeDTO = new CoordonneeDTO();
            coordonneeDTO.setId(incident.getCoordonnee().getId());
            coordonneeDTO.setLatitude(incident.getCoordonnee().getLatitude());
            coordonneeDTO.setLongitude(incident.getCoordonnee().getLongitude());
            coordonneeDTO.setOrdre(incident.getCoordonnee().getOrdre());

            if (incident.getCoordonnee().getSite() != null) {
                Site site = incident.getCoordonnee().getSite();
                coordonneeDTO.setSiteId(site.getId());

                // Fix: Site doesn't have getNom(), so get name from Wilaya instead
                if (site.getWilaya() != null) {
                    coordonneeDTO.setSiteName("Site - " + site.getWilaya().getNom());
                } else {
                    coordonneeDTO.setSiteName("Site #" + site.getId());
                }
            }

            response.setCoordonnee(coordonneeDTO);
        }

        return response;
    }
}