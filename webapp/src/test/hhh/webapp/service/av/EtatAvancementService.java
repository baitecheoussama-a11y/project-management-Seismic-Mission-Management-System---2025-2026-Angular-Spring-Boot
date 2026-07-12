// service/EtatAvancementService.java
package com.pfe.webapp.service.av;

import com.pfe.webapp.dto.av.AvancementDTO;
import com.pfe.webapp.dto.av.AvancementRequestDTO;
import com.pfe.webapp.dto.av.EtatAvancementDTO;
import com.pfe.webapp.dto.av.UpdateStatusRequestDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.*;
import com.pfe.webapp.repository.av.AvancementRepository;
import com.pfe.webapp.repository.av.EtatAvancementRepository;
import com.pfe.webapp.repository.team.ActiveRepository;
import com.pfe.webapp.service.ProjectService;
import com.pfe.webapp.service.project.ProjectProgressionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EtatAvancementService {

    @Autowired
    private EtatAvancementRepository etatAvancementRepository;

    @Autowired
    private AvancementRepository avancementRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ActiveRepository activeRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProjectProgressionService progressionService;  // Make sure this is injected

    // ============ ETAT AVANCEMENT CRUD ============

    @Transactional(readOnly = true)
    public List<EtatAvancementDTO> getEtatAvancementsByProject(Long projectId) {
        return etatAvancementRepository.findByProjectIdWithAvancements(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EtatAvancementDTO getEtatAvancementById(Long id) {
        EtatAvancement etat = etatAvancementRepository.findByIdWithAvancements(id)
                .orElseThrow(() -> new RuntimeException("EtatAvancement not found with id: " + id));
        return convertToDTO(etat);
    }

    @Transactional
    public EtatAvancementDTO createEtatAvancementForProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        EtatAvancement etat = new EtatAvancement();
        etat.setProject(project);
        etat.setStatus(StatusEtatAvancement.PLANIFIER);
        etat.setDateLastAvancement(LocalDate.now());

        EtatAvancement saved = etatAvancementRepository.save(etat);
        return convertToDTO(saved);
    }

    @Transactional
    public EtatAvancementDTO createEtatAvancementForActive(Long activeId, Long projectId) {
        Active active = activeRepository.findById(activeId)
                .orElseThrow(() -> new RuntimeException("Active not found with id: " + activeId));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        // Check if already exists
        if (etatAvancementRepository.findByProjectIdAndActiveId(projectId, activeId).isPresent()) {
            throw new RuntimeException("EtatAvancement already exists for this active and project");
        }

        EtatAvancement etat = new EtatAvancement();
        etat.setActive(active);
        etat.setProject(project);
        etat.setStatus(StatusEtatAvancement.PLANIFIER);
        etat.setDateLastAvancement(LocalDate.now());

        EtatAvancement saved = etatAvancementRepository.save(etat);
        return convertToDTO(saved);
    }

// In EtatAvancementService.java - update the updateEtatAvancementStatus method


    @Transactional
    public EtatAvancementDTO updateEtatAvancementStatus(Long id, String status) {
        EtatAvancement etat = etatAvancementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EtatAvancement not found with id: " + id));

        etat.setStatus(StatusEtatAvancement.valueOf(status));
        etat.setDateLastAvancement(LocalDate.now());

        EtatAvancement updated = etatAvancementRepository.save(etat);

        // Case 1: This is a PROJECT status (active is null)
        if (etat.getActive() == null && etat.getProject() != null) {
            progressionService.updateProjectProgressionFromStatus(etat.getProject());
        }

        // Case 2: This is an ACTIVITY status (active is not null)
        if (etat.getActive() != null && etat.getProject() != null) {
            Long projectId = etat.getProject().getId();
            progressionService.updateProjectProgressionFromActivities(projectId);
        }

        return convertToDTO(updated);
    }
    @Transactional
    public void deleteEtatAvancement(Long id) {
        EtatAvancement etat = etatAvancementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EtatAvancement not found with id: " + id));

        // Delete all avancements first
        avancementRepository.deleteByEtatAvancementId(id);
        etatAvancementRepository.delete(etat);
    }

    // ============ AVANCEMENT CRUD ============

    @Transactional
    public AvancementDTO addAvancement(Long etatAvancementId, AvancementRequestDTO request) {
        EtatAvancement etat = etatAvancementRepository.findById(etatAvancementId)
                .orElseThrow(() -> new RuntimeException("EtatAvancement not found with id: " + etatAvancementId));

        Avancement avancement = new Avancement();
        avancement.setTitre(request.getTitre());
        avancement.setDate(request.getDate());
        avancement.setResume(request.getResume());
        avancement.setEtatAvancement(etat);

        Avancement saved = avancementRepository.save(avancement);

        // Update last avancement date
        etat.setDateLastAvancement(request.getDate());
        etatAvancementRepository.save(etat);

        return convertAvancementToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<AvancementDTO> getAvancementsByEtatAvancement(Long etatAvancementId) {
        return avancementRepository.findByEtatAvancementId(etatAvancementId).stream()
                .map(this::convertAvancementToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AvancementDTO updateAvancement(Long id, AvancementRequestDTO request) {
        Avancement avancement = avancementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avancement not found with id: " + id));

        avancement.setTitre(request.getTitre());
        avancement.setDate(request.getDate());
        avancement.setResume(request.getResume());

        // Update parent's last avancement date
        EtatAvancement etat = avancement.getEtatAvancement();
        if (etat != null) {
            etat.setDateLastAvancement(request.getDate());
            etatAvancementRepository.save(etat);
        }

        Avancement updated = avancementRepository.save(avancement);
        return convertAvancementToDTO(updated);
    }

    @Transactional
    public void deleteAvancement(Long id) {
        if (!avancementRepository.existsById(id)) {
            throw new RuntimeException("Avancement not found with id: " + id);
        }
        avancementRepository.deleteById(id);
    }

    // ============ CONVERTERS ============

    private EtatAvancementDTO convertToDTO(EtatAvancement etat) {
        EtatAvancementDTO dto = new EtatAvancementDTO();
        dto.setId(etat.getId());
        dto.setDateLastAvancement(etat.getDateLastAvancement());
        dto.setStatus(etat.getStatus());

        if (etat.getProject() != null) {
            dto.setProjectId(etat.getProject().getId());
            dto.setProjectName(etat.getProject().getNom());
        }

        if (etat.getActive() != null) {
            dto.setActiveId(etat.getActive().getId());
            dto.setActiveCode(etat.getActive().getCodeActive());
        }

        if (etat.getAvancements() != null && !etat.getAvancements().isEmpty()) {
            dto.setAvancements(etat.getAvancements().stream()
                    .map(this::convertAvancementToDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private AvancementDTO convertAvancementToDTO(Avancement avancement) {
        AvancementDTO dto = new AvancementDTO();
        dto.setId(avancement.getId());
        dto.setTitre(avancement.getTitre());
        dto.setDate(avancement.getDate());
        dto.setResume(avancement.getResume());

        if (avancement.getEtatAvancement() != null) {
            dto.setEtatAvancementId(avancement.getEtatAvancement().getId());
        }

        return dto;
    }

    // service/av/EtatAvancementService.java - أضف هذه الدوال






    // Get etat avancement by active ID and mission ID
    public EtatAvancementDTO getEtatAvancementByActiveAndMission(Long activeId, Long missionId) {
        Optional<EtatAvancement> etatOpt = etatAvancementRepository.findByActiveIdAndMissionId(activeId, missionId);

        if (etatOpt.isPresent()) {
            return convertToDTO(etatOpt.get());
        }

        return null;
    }

    // Create etat avancement for active and mission
    @Transactional
    public EtatAvancementDTO createEtatAvancementForActiveAndMission(Long activeId, Long missionId) {
        // Find mission
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        // Find active
        Active active = activeRepository.findById(activeId)
                .orElseThrow(() -> new RuntimeException("Active not found with id: " + activeId));

        // Check if already exists
        Optional<EtatAvancement> existing = etatAvancementRepository.findByActiveIdAndMissionId(activeId, missionId);
        if (existing.isPresent()) {
            return convertToDTO(existing.get());
        }

        // Get current active project for this mission
        Project project = projectRepository.findCurrentActiveProjectByMissionId(missionId)
                .orElseThrow(() -> new RuntimeException("No active project found for this mission"));

        EtatAvancement etat = new EtatAvancement();
        etat.setActive(active);
        etat.setProject(project);
        etat.setStatus(StatusEtatAvancement.PLANIFIER);
        etat.setDateLastAvancement(LocalDate.now());

        EtatAvancement saved = etatAvancementRepository.save(etat);
        return convertToDTO(saved);
    }

















}