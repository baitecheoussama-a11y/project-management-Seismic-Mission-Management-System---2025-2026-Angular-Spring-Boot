package com.pfe.webapp.service;

import com.pfe.webapp.dto.event.EvenementRequestDTO;
import com.pfe.webapp.dto.event.EvenementResponseDTO;
import com.pfe.webapp.entity.Evenement;
import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.entity.TypeEvenement;
import com.pfe.webapp.repository.event.EvenementRepository;
import com.pfe.webapp.repository.MissionRepository;
import com.pfe.webapp.repository.event.TypeEvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvenementServiceImpl implements EvenementService {

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private TypeEvenementRepository typeEvenementRepository;

    // ========== Helper Method: Convert Entity to Response DTO ==========
    private EvenementResponseDTO convertToResponseDTO(Evenement evenement) {
        if (evenement == null) return null;

        Long missionId = null;
        String missionNom = null;
        if (evenement.getMission() != null) {
            missionId = evenement.getMission().getId();
            missionNom = evenement.getMission().getNom();
        }

        Long typeId = null;
        String typeNom = null;
        String niveauPriorite = null;
        String niveauPrioriteLabel = null;
        String niveauPrioriteColor = null;

        if (evenement.getTypeEvenement() != null) {
            typeId = evenement.getTypeEvenement().getId();
            typeNom = evenement.getTypeEvenement().getNom();

            if (evenement.getTypeEvenement().getNiveauPriorite() != null) {
                niveauPriorite = evenement.getTypeEvenement().getNiveauPriorite().name();
                niveauPrioriteLabel = evenement.getTypeEvenement().getNiveauPriorite().getLabel();
                niveauPrioriteColor = evenement.getTypeEvenement().getNiveauPriorite().getColor();
            }
        }

        return new EvenementResponseDTO(
                evenement.getId(),
                evenement.getTitre(),
                evenement.getDescription(),
                evenement.getDate(),
                evenement.getHeure(),
                missionId,
                missionNom,
                typeId,
                typeNom,
                niveauPriorite,
                niveauPrioriteLabel,
                niveauPrioriteColor
        );
    }

    // ========== Create ==========
    @Override
    public EvenementResponseDTO createEvenement(EvenementRequestDTO requestDTO) {
        // Validate input
        if (requestDTO.getTitre() == null || requestDTO.getTitre().trim().isEmpty()) {
            throw new RuntimeException("Event title is required");
        }

        if (requestDTO.getDate() == null) {
            throw new RuntimeException("Event date is required");
        }

        if (requestDTO.getTypeEvenementId() == null) {
            throw new RuntimeException("Event type is required");
        }

        // Get Mission (if provided)
        Mission mission = null;
        if (requestDTO.getMissionId() != null) {
            mission = missionRepository.findById(requestDTO.getMissionId())
                    .orElseThrow(() -> new RuntimeException("Mission not found with id: " + requestDTO.getMissionId()));
        }

        // Get Type Evenement
        TypeEvenement typeEvenement = typeEvenementRepository.findById(requestDTO.getTypeEvenementId())
                .orElseThrow(() -> new RuntimeException("Event type not found with id: " + requestDTO.getTypeEvenementId()));

        // Create new Evenement
        Evenement evenement = new Evenement();
        evenement.setTitre(requestDTO.getTitre());
        evenement.setDescription(requestDTO.getDescription());
        evenement.setDate(requestDTO.getDate());
        evenement.setHeure(requestDTO.getHeure());
        evenement.setMission(mission);
        evenement.setTypeEvenement(typeEvenement);

        // Save to database
        Evenement savedEvenement = evenementRepository.save(evenement);

        // Return response
        return convertToResponseDTO(savedEvenement);
    }

    // ========== Update ==========
    @Override
    public EvenementResponseDTO updateEvenement(Long id, EvenementRequestDTO requestDTO) {
        // Find existing event
        Evenement existingEvenement = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        // Update fields
        if (requestDTO.getTitre() != null && !requestDTO.getTitre().trim().isEmpty()) {
            existingEvenement.setTitre(requestDTO.getTitre());
        }

        if (requestDTO.getDescription() != null) {
            existingEvenement.setDescription(requestDTO.getDescription());
        }

        if (requestDTO.getDate() != null) {
            existingEvenement.setDate(requestDTO.getDate());
        }

        if (requestDTO.getHeure() != null) {
            existingEvenement.setHeure(requestDTO.getHeure());
        }

        // Update Mission if provided
        if (requestDTO.getMissionId() != null) {
            Mission mission = missionRepository.findById(requestDTO.getMissionId())
                    .orElseThrow(() -> new RuntimeException("Mission not found with id: " + requestDTO.getMissionId()));
            existingEvenement.setMission(mission);
        } else {
            existingEvenement.setMission(null);
        }

        // Update Type Evenement if provided
        if (requestDTO.getTypeEvenementId() != null) {
            TypeEvenement typeEvenement = typeEvenementRepository.findById(requestDTO.getTypeEvenementId())
                    .orElseThrow(() -> new RuntimeException("Event type not found with id: " + requestDTO.getTypeEvenementId()));
            existingEvenement.setTypeEvenement(typeEvenement);
        }

        // Save updated event
        Evenement updatedEvenement = evenementRepository.save(existingEvenement);

        // Return response
        return convertToResponseDTO(updatedEvenement);
    }

    // ========== Delete ==========
    @Override
    public void deleteEvenement(Long id) {
        if (!evenementRepository.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        evenementRepository.deleteById(id);
    }

    // ========== Get By ID ==========
    @Override
    public EvenementResponseDTO getEvenementById(Long id) {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
        return convertToResponseDTO(evenement);
    }

    // ========== Get All ==========
    @Override
    public List<EvenementResponseDTO> getAllEvenements() {
        return evenementRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== Get By Mission ==========
    @Override
    public List<EvenementResponseDTO> getEvenementsByMission(Long missionId) {
        return evenementRepository.findByMissionId(missionId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== Get By Type ==========
    @Override
    public List<EvenementResponseDTO> getEvenementsByType(Long typeEvenementId) {
        return evenementRepository.findByTypeEvenementId(typeEvenementId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== Get By Date ==========
    @Override
    public List<EvenementResponseDTO> getEvenementsByDate(LocalDate date) {
        return evenementRepository.findByDate(date).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== Get By Date Range ==========
    @Override
    public List<EvenementResponseDTO> getEvenementsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new RuntimeException("Start date and end date are required");
        }
        return evenementRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== Get Upcoming Events ==========
    @Override
    public List<EvenementResponseDTO> getUpcomingEvenements() {
        return evenementRepository.findUpcomingEvents(LocalDate.now()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== Get Past Events ==========
    @Override
    public List<EvenementResponseDTO> getPastEvenements() {
        return evenementRepository.findPastEvents(LocalDate.now()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== Get Today's Events ==========
    @Override
    public List<EvenementResponseDTO> getTodaysEvenements() {
        return evenementRepository.findTodaysEvents(LocalDate.now()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== Search Events ==========
    @Override
    public List<EvenementResponseDTO> searchEvenements(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllEvenements();
        }
        return evenementRepository.searchByKeyword(keyword.trim()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== Get Total Count ==========
    @Override
    public long getTotalEvenementsCount() {
        return evenementRepository.count();
    }
}