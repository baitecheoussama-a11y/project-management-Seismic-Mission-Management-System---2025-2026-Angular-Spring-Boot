package com.pfe.webapp.service.av;

import com.pfe.webapp.dto.av.AvancementDTO;
import com.pfe.webapp.dto.av.AvancementRequestDTO;
import com.pfe.webapp.dto.av.EtatAvancementDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.*;
import com.pfe.webapp.repository.av.AvancementRepository;
import com.pfe.webapp.repository.av.EtatAvancementRepository;
import com.pfe.webapp.repository.team.ActiveRepository;
import com.pfe.webapp.repository.team.AffectationEquipeRepository;
import com.pfe.webapp.service.ProjectService;
import com.pfe.webapp.service.project.ProjectProgressionService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AffectationEquipeRepository affectationEquipeRepository;

    @Autowired
    private ProjectProgressionService progressionService;

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

    // ✅ NEW: Update status with automatic determination based on real dates
    @Transactional
    public EtatAvancementDTO updateEtatAvancementStatus(Long id, String status) {
        EtatAvancement etat = etatAvancementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EtatAvancement not found with id: " + id));

        StatusEtatAvancement newStatus = StatusEtatAvancement.valueOf(status);

        // If status is ANNULE, just set it and don't auto-determine
        if (newStatus == StatusEtatAvancement.ANNULE) {
            etat.setStatus(newStatus);
            etat.setDateLastAvancement(LocalDate.now());
            EtatAvancement updated = etatAvancementRepository.save(etat);
            updateProjectProgression(etat);
            return convertToDTO(updated);
        }

        // For other statuses, auto-determine based on real dates
        StatusEtatAvancement determinedStatus = determineStatusFromDates(etat.getActive(), etat.getProject());

        // If user manually set a status that's not ANNULE, we respect it but also check real dates
        // For PLANIFIER and ENATTENTE, we keep them as user wants but check if they should be ENCOURS or ENRETARD
        if (newStatus == StatusEtatAvancement.PLANIFIER || newStatus == StatusEtatAvancement.ENATTENTE) {
            // Keep user's choice but if dates indicate otherwise, we might override
            if (determinedStatus == StatusEtatAvancement.ENCOURS || determinedStatus == StatusEtatAvancement.ENRETARD) {
                // User manually set to PLANIFIER or ENATTENTE but real dates suggest otherwise
                // We keep user's choice but log it
                System.out.println("Warning: User set status to " + newStatus + " but real dates suggest " + determinedStatus);
            }
            etat.setStatus(newStatus);
        } else {
            // For ENCOURS, ENRETARD, TERMINI - auto-determine
            etat.setStatus(determinedStatus);
        }

        etat.setDateLastAvancement(LocalDate.now());
        EtatAvancement updated = etatAvancementRepository.save(etat);

        // Update real dates based on status
        updateRealDatesFromStatus(etat);

        // Update project progression
        updateProjectProgression(etat);

        return convertToDTO(updated);
    }

    // ✅ NEW: Determine status based on real dates
    private StatusEtatAvancement determineStatusFromDates(Active active, Project project) {
        if (active == null || project == null) {
            return StatusEtatAvancement.PLANIFIER;
        }

        // Get the assignment for this active in the mission
        Long missionId = project.getMission().getId();
        Optional<AffectationEquipe> assignmentOpt = affectationEquipeRepository
                .findByEquipeIdAndMissionIdAndActiveId(null, missionId, active.getId())
                .stream()
                .findFirst();

        if (assignmentOpt.isEmpty()) {
            return StatusEtatAvancement.PLANIFIER;
        }

        AffectationEquipe assignment = assignmentOpt.get();
        LocalDate dateStartReelle = assignment.getDateStartReelle();
        LocalDate dateFinReelle = assignment.getDateFinReelle();
        LocalDate dateFin = assignment.getDateFin();
        LocalDate now = LocalDate.now();

        // If end date is filled -> TERMINI
        if (dateFinReelle != null) {
            return StatusEtatAvancement.TERMINI;
        }

        // If start date is filled -> ENCOURS
        if (dateStartReelle != null) {
            // Check if it's delayed (target end date passed)
            if (dateFin != null && now.isAfter(dateFin)) {
                return StatusEtatAvancement.ENRETARD;
            }
            return StatusEtatAvancement.ENCOURS;
        }

        // No start date -> PLANIFIER (default)
        return StatusEtatAvancement.PLANIFIER;
    }

    // ✅ NEW: Update real dates based on status
    private void updateRealDatesFromStatus(EtatAvancement etat) {
        if (etat.getActive() == null || etat.getProject() == null) {
            return;
        }

        Long missionId = etat.getProject().getMission().getId();
        Optional<AffectationEquipe> assignmentOpt = affectationEquipeRepository
                .findByEquipeIdAndMissionIdAndActiveId(null, missionId, etat.getActive().getId())
                .stream()
                .findFirst();

        if (assignmentOpt.isEmpty()) {
            return;
        }

        AffectationEquipe assignment = assignmentOpt.get();
        LocalDate now = LocalDate.now();
        StatusEtatAvancement status = etat.getStatus();

        switch (status) {
            case ENCOURS:
            case ENRETARD:
                if (assignment.getDateStartReelle() == null) {
                    assignment.setDateStartReelle(now);
                }
                break;
            case TERMINI:
                if (assignment.getDateFinReelle() == null) {
                    assignment.setDateFinReelle(now);
                }
                if (assignment.getDateStartReelle() == null) {
                    assignment.setDateStartReelle(now);
                }
                break;
            case ANNULE:
                if (assignment.getDateFinReelle() == null) {
                    assignment.setDateFinReelle(now);
                }
                break;
            default:
                // PLANIFIER or ENATTENTE - do nothing to real dates
                break;
        }

        affectationEquipeRepository.save(assignment);
    }

    // ✅ NEW: Update real dates when assignment dates change
    @Transactional
    public void updateStatusFromRealDates(Long activeId, Long missionId) {
        Optional<EtatAvancement> etatOpt = etatAvancementRepository.findByActiveIdAndMissionId(activeId, missionId);
        if (etatOpt.isEmpty()) {
            return;
        }

        EtatAvancement etat = etatOpt.get();

        // Don't auto-update if status is ANNULE
        if (etat.getStatus() == StatusEtatAvancement.ANNULE) {
            return;
        }

        StatusEtatAvancement determinedStatus = determineStatusFromDates(etat.getActive(), etat.getProject());

        // Only update if status changed
        if (determinedStatus != etat.getStatus()) {
            etat.setStatus(determinedStatus);
            etat.setDateLastAvancement(LocalDate.now());
            etatAvancementRepository.save(etat);
            updateProjectProgression(etat);
        }
    }

    private void updateProjectProgression(EtatAvancement etat) {
        if (etat.getActive() == null && etat.getProject() != null) {
            // Project status
            progressionService.updateProjectProgressionFromStatus(etat.getProject());
        } else if (etat.getActive() != null && etat.getProject() != null) {
            // Activity status
            Long projectId = etat.getProject().getId();
            progressionService.updateProjectProgressionFromActivities(projectId);
        }
    }

    @Transactional
    public void deleteEtatAvancement(Long id) {
        EtatAvancement etat = etatAvancementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EtatAvancement not found with id: " + id));

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
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        Active active = activeRepository.findById(activeId)
                .orElseThrow(() -> new RuntimeException("Active not found with id: " + activeId));

        Optional<EtatAvancement> existing = etatAvancementRepository.findByActiveIdAndMissionId(activeId, missionId);
        if (existing.isPresent()) {
            return convertToDTO(existing.get());
        }

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

    // ✅ NEW: Update real dates and auto-determine status
    @Transactional
    public void updateRealDatesAndStatus(Long activeId, Long missionId, LocalDate dateStartReelle, LocalDate dateFinReelle) {
        // Find the assignment
        Optional<AffectationEquipe> assignmentOpt = affectationEquipeRepository
                .findAllByEquipeIdAndMissionIdWithDetails(null, missionId)
                .stream()
                .filter(a -> a.getActive().getId().equals(activeId))
                .findFirst();

        if (assignmentOpt.isEmpty()) {
            throw new RuntimeException("Assignment not found for active: " + activeId);
        }

        AffectationEquipe assignment = assignmentOpt.get();

        // Update real dates
        if (dateStartReelle != null) {
            assignment.setDateStartReelle(dateStartReelle);
        }
        if (dateFinReelle != null) {
            assignment.setDateFinReelle(dateFinReelle);
        }

        affectationEquipeRepository.save(assignment);

        // Auto-determine status
        updateStatusFromRealDates(activeId, missionId);
    }
}