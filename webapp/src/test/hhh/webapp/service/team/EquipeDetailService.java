// service/team/EquipeDetailService.java
package com.pfe.webapp.service.team;

import com.pfe.webapp.dto.*;
import com.pfe.webapp.dto.team.*;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.*;
import com.pfe.webapp.repository.rapport.RapportRepository;
import com.pfe.webapp.repository.rapport.RendementRepository;
import com.pfe.webapp.repository.team.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipeDetailService {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private AffectationEmployeRepository affectationEmployeRepository;

    @Autowired
    private AffectationEquipeRepository affectationEquipeRepository;

    @Autowired
    private ActiveRepository activeRepository;

    @Autowired
    private RapportRepository rapportRepository;

    @Autowired
    private RendementRepository rendementRepository;

    @Transactional(readOnly = true)
    public EquipeDetailDTO getEquipeDetail(Long equipeId, Long missionId) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe not found with id: " + equipeId));

        EquipeDetailDTO dto = new EquipeDetailDTO();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setType(equipe.getType().name());

        // Get members
        List<AffectationEmploye> affectations = affectationEmployeRepository
                .findByEquipeIdAndMissionId(equipeId, missionId);

        List<EmployeSimpleDTO> membres = affectations.stream()
                .map(ae -> {
                    Employe emp = ae.getEmploye();
                    EmployeSimpleDTO empDto = new EmployeSimpleDTO();
                    empDto.setId(emp.getId());
                    empDto.setNom(emp.getNom());
                    empDto.setPrenom(emp.getPrenom());
                    empDto.setEmail(emp.getEmail());
                    empDto.setNumTel(emp.getNumTel());
                    empDto.setPoste("");
                    return empDto;
                })
                .collect(Collectors.toList());
        dto.setMembres(membres);

        // Get activities - using findAllByEquipeIdAndMissionId to get all assignments
        List<AffectationEquipe> affectationEquipes = affectationEquipeRepository
                .findAllByEquipeIdAndMissionId(equipeId, missionId);

        List<ActiveDTO> activitesDTO = affectationEquipes.stream()
                .map(ae -> {
                    ActiveDTO activeDTO = new ActiveDTO();
                    Active active = ae.getActive();
                    if (active != null) {
                        activeDTO.setId(active.getId());
                        activeDTO.setCodeActive(active.getCodeActive());
                        activeDTO.setObjectif(active.getObjectif());
                        activeDTO.setDescription(active.getDescription());
                    }
                    activeDTO.setDateDebut(ae.getDateDebut());
                    activeDTO.setDateFin(ae.getDateFin());

                    // Calculate progression based on rendements
                    double progression = calculateProgression(ae.getId());
                    activeDTO.setProgression(progression);

                    return activeDTO;
                })
                .collect(Collectors.toList());
        dto.setActivites(activitesDTO);

        // Get rapports
        List<Rapport> rapports = rapportRepository.findRapportsByEquipeId(equipeId);
        List<RapportDTO> rapportsDTO = rapports.stream().map(rapport -> {
            RapportDTO rapportDTO = new RapportDTO();
            rapportDTO.setId(rapport.getId());
            rapportDTO.setDate(rapport.getDate());
            rapportDTO.setTitre(rapport.getTitre());
            rapportDTO.setResume(rapport.getResume());
            return rapportDTO;
        }).collect(Collectors.toList());
        dto.setRapports(rapportsDTO);

        // Get rendements
        List<Rendement> rendements = rendementRepository.findRendementsByEquipeId(equipeId);
        List<RendementDTO> rendementsDTO = rendements.stream().map(rend -> {
            RendementDTO rendDTO = new RendementDTO();
            rendDTO.setId(rend.getId());
            rendDTO.setHeureDebut(rend.getHeureDebut());
            rendDTO.setHeureFin(rend.getHeureFin());
            rendDTO.setValeurRendement(rend.getValeurRendement());
            rendDTO.setUniteRendement(rend.getUniteRendement());
            rendDTO.setDate(rend.getDate());
            return rendDTO;
        }).collect(Collectors.toList());
        dto.setRendements(rendementsDTO);

        // Calculate statistics
        StatActivitesDTO stats = new StatActivitesDTO();
        stats.setTotalActivites(activitesDTO.size());
        stats.setTotalRapports(rapportsDTO.size());
        stats.setTotalRendements(rendementsDTO.size());

        Double avgRendement = rendementRepository.getAverageRendementByEquipeId(equipeId);
        stats.setMoyenneRendement(avgRendement != null ? avgRendement : 0.0);

        double totalHeures = rendementsDTO.stream()
                .mapToDouble(r -> {
                    if (r.getHeureDebut() != null && r.getHeureFin() != null) {
                        return (double) (r.getHeureFin().getHour() - r.getHeureDebut().getHour()) +
                                (r.getHeureFin().getMinute() - r.getHeureDebut().getMinute()) / 60.0;
                    }
                    return 0.0;
                })
                .sum();
        stats.setTotalHeuresTravaillees(totalHeures);

        dto.setStatistiques(stats);

        return dto;
    }

    private double calculateProgression(Long affectationEquipeId) {
        if (affectationEquipeId == null) return 0.0;

        // Calculate progression based on completed rendements vs target
        List<Rendement> rendements = rendementRepository.findAll()
                .stream()
                .filter(r -> r.getAffectationEquipe() != null &&
                        r.getAffectationEquipe().getId().equals(affectationEquipeId))
                .collect(Collectors.toList());

        if (rendements.isEmpty()) return 0.0;

        // Assume target is 100% when sum of rendement values reaches 1000
        double totalRendement = rendements.stream()
                .mapToDouble(Rendement::getValeurRendement)
                .sum();

        return Math.min(100.0, (totalRendement / 1000.0) * 100.0);
    }

    // Get all equipes with their activities
    public List<EquipeActivitiesDTO> getAllEquipesWithActivities() {
        List<Equipe> equipes = equipeRepository.findAll();
        return equipes.stream()
                .map(this::convertToEquipeActivitiesDTO)
                .collect(Collectors.toList());
    }

    // Get activities for a specific equipe and mission
    public List<ActiveDTO> getActivitiesByEquipeId(Long equipeId, Long missionId) {
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new RuntimeException("Equipe not found with id: " + equipeId));

        // Get all affectationEquipes for this equipe and mission
        List<AffectationEquipe> affectationEquipes = affectationEquipeRepository
                .findAllByEquipeIdAndMissionId(equipeId, missionId);

        if (affectationEquipes.isEmpty()) {
            return new ArrayList<>();
        }

        return affectationEquipes.stream()
                .map(ae -> {
                    ActiveDTO dto = new ActiveDTO();
                    Active active = ae.getActive();
                    if (active != null) {
                        dto.setId(active.getId());
                        dto.setCodeActive(active.getCodeActive());
                        dto.setObjectif(active.getObjectif());
                        dto.setDescription(active.getDescription());
                    }
                    dto.setDateDebut(ae.getDateDebut());
                    dto.setDateFin(ae.getDateFin());

                    // Calculate progression
                    double progression = calculateProgression(ae.getId());
                    dto.setProgression(progression);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Helper method to convert Equipe to EquipeActivitiesDTO
    private EquipeActivitiesDTO convertToEquipeActivitiesDTO(Equipe equipe) {
        EquipeActivitiesDTO dto = new EquipeActivitiesDTO();
        dto.setId(equipe.getId());
        dto.setNom(equipe.getNom());
        dto.setType(equipe.getType());

        long memberCount = affectationEmployeRepository.countActiveMembersByEquipe(equipe.getId(), LocalDate.now());
        dto.setMemberCount((int) memberCount);

        return dto;
    }
}