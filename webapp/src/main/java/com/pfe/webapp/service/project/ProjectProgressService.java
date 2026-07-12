package com.pfe.webapp.service.project;

import com.pfe.webapp.dto.project.ActivityProgressDTO;
import com.pfe.webapp.dto.project.ProjectProgressStatsDTO;
import com.pfe.webapp.entity.Active;
import com.pfe.webapp.entity.AffectationEquipe;
import com.pfe.webapp.entity.Rapport;
import com.pfe.webapp.entity.Rendement;
import com.pfe.webapp.repository.project.ProjectProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProjectProgressService {

    @Autowired
    private ProjectProgressRepository projectProgressRepository;

    /**
     * Calculate status based on dates - SAME logic as EquipeService
     */
    private String calculateStatusFromAffectation(AffectationEquipe assignment) {
        if (assignment == null) {
            return "PLANIFIER";
        }

        LocalDate dateDebut = assignment.getDateDebut();
        LocalDate dateFin = assignment.getDateFin();
        LocalDate dateStartReelle = assignment.getDateStartReelle();
        LocalDate dateFinReelle = assignment.getDateFinReelle();
        LocalDate now = LocalDate.now();

        // Case 1: No dates at all -> ANNULE (Cancelled)
        if (dateDebut == null && dateFin == null && dateStartReelle == null && dateFinReelle == null) {
            return "ANNULE";
        }

        // Case 2: dateFinReelle is set -> TERMINI (Completed)
        if (dateFinReelle != null) {
            return "TERMINI";
        }

        // Case 3: dateStartReelle is set
        if (dateStartReelle != null) {
            // If dateStartReelle is in the future -> ENATTENTE (On Hold)
            if (dateStartReelle.isAfter(now)) {
                return "ENATTENTE";
            }
            // If dateStartReelle is today or past -> IN PROGRESS
            // Check if delayed (target end date passed)
            if (dateFin != null && now.isAfter(dateFin)) {
                return "ENRETARD";
            }
            return "ENCOURS";
        }

        // Case 4: No dateStartReelle but has dateDebut and/or dateFin -> PLANIFIER (Planned)
        return "PLANIFIER";
    }

    /**
     * Calculate progression based on status - SAME logic as EquipeService
     */
    private Double calculateProgressionFromStatus(String status) {
        switch (status) {
            case "TERMINI":
                return 100.0;
            case "ENCOURS":
                return 50.0;
            case "ENRETARD":
                return 40.0;
            case "ENATTENTE":
                return 25.0;
            case "PLANIFIER":
                return 0.0;
            case "ANNULE":
                return 0.0;
            default:
                return 0.0;
        }
    }

    public ProjectProgressStatsDTO getProjectProgressStats(Long projectId) {
        ProjectProgressStatsDTO stats = new ProjectProgressStatsDTO();

        try {
            // 1. Get reports (Rapport -> Project)
            List<Rapport> rapports = projectProgressRepository.findRapportsByProjectId(projectId);
            stats.setTotalReports(rapports != null ? rapports.size() : 0);

            // 2. Get all Rendements through project
            List<Rendement> rendements = projectProgressRepository.findRendementsByProjectId(projectId);
            stats.setTotalRendements(rendements != null ? rendements.size() : 0);

            // 3. Calculate total work hours and productivity from all rendements
            double totalWorkHours = 0;
            double totalProductivity = 0;

            if (rendements != null) {
                for (Rendement rend : rendements) {
                    totalWorkHours += rend.getDureeHeures() != null ? rend.getDureeHeures() : 0;
                    totalProductivity += rend.getValeurRendement() != null ? rend.getValeurRendement() : 0;
                }
            }

            stats.setTotalWorkHours(totalWorkHours);
            stats.setTotalProductivityValue(totalProductivity);
            stats.setAverageProductivity((rendements == null || rendements.isEmpty()) ? 0 : totalProductivity / rendements.size());

            // 4. Get actives through AffectationEquipe
            List<Active> actives = projectProgressRepository.findActivesByProjectId(projectId);
            stats.setTotalActivities(actives != null ? actives.size() : 0);

            if (actives == null || actives.isEmpty()) {
                stats.setCompletedActivities(0);
                stats.setInProgressActivities(0);
                stats.setPendingActivities(0);
                stats.setDelayedActivities(0);
                stats.setActivitiesProgress(new ArrayList<>());
                return stats;
            }

            // 5. Get AffectationEquipe for all actives in this project
            List<Long> activeIds = actives.stream()
                    .map(Active::getId)
                    .collect(Collectors.toList());

            List<AffectationEquipe> affectationEquipes = projectProgressRepository.findAffectationEquipesByActiveIdsAndProject(activeIds, projectId);
            Map<Long, AffectationEquipe> affectationMap = affectationEquipes.stream()
                    .collect(Collectors.toMap(
                            ae -> ae.getActive().getId(),
                            ae -> ae,
                            (existing, replacement) -> existing
                    ));

            // Group rendements by active
            Map<Long, List<Rendement>> rendementsByActive = new HashMap<>();
            if (rendements != null) {
                for (Rendement rend : rendements) {
                    if (rend.getAffectationEquipe() != null &&
                            rend.getAffectationEquipe().getActive() != null) {
                        Long activeId = rend.getAffectationEquipe().getActive().getId();
                        rendementsByActive.computeIfAbsent(activeId, k -> new ArrayList<>()).add(rend);
                    }
                }
            }

            // Calculate activity counts by status
            int completedCount = 0;
            int inProgressCount = 0;
            int pendingCount = 0;
            int delayedCount = 0;

            List<ActivityProgressDTO> activitiesProgress = new ArrayList<>();

            for (Active active : actives) {
                ActivityProgressDTO dto = new ActivityProgressDTO();
                dto.setActiveId(active.getId());
                dto.setCodeActive(active.getCodeActive());
                dto.setObjectif(active.getObjectif());

                AffectationEquipe affectation = affectationMap.get(active.getId());

                // Use the SAME status calculation
                String status = calculateStatusFromAffectation(affectation);
                dto.setStatus(status);

                // Use the SAME progression calculation - FIXED conversion
                double progression = calculateProgressionFromStatus(status);
                dto.setCompletionPercentage((int) Math.round(progression));

                // Count statuses
                switch (status) {
                    case "TERMINI":
                        completedCount++;
                        break;
                    case "ENCOURS":
                        inProgressCount++;
                        break;
                    case "ENRETARD":
                        delayedCount++;
                        break;
                    case "ENATTENTE":
                        pendingCount++;
                        break;
                    case "ANNULE":
                        pendingCount++; // Cancelled is also considered pending
                        break;
                    default:
                        pendingCount++;
                }

                // Calculate productivity and work hours for this activity from rendements
                double activityProductivity = 0;
                double activityWorkHours = 0;
                int activityRendementCount = 0;

                List<Rendement> activityRendements = rendementsByActive.getOrDefault(active.getId(), new ArrayList<>());
                for (Rendement rend : activityRendements) {
                    activityProductivity += rend.getValeurRendement() != null ? rend.getValeurRendement() : 0;
                    activityWorkHours += rend.getDureeHeures() != null ? rend.getDureeHeures() : 0;
                    activityRendementCount++;
                }

                dto.setProductivityValue(activityProductivity);
                dto.setTotalWorkHours(activityWorkHours);
                dto.setRendementCount(activityRendementCount);

                activitiesProgress.add(dto);
            }

            stats.setCompletedActivities(completedCount);
            stats.setInProgressActivities(inProgressCount);
            stats.setPendingActivities(pendingCount);
            stats.setDelayedActivities(delayedCount);
            stats.setActivitiesProgress(activitiesProgress);

        } catch (Exception e) {
            e.printStackTrace();
            stats.setTotalActivities(0);
            stats.setCompletedActivities(0);
            stats.setInProgressActivities(0);
            stats.setPendingActivities(0);
            stats.setDelayedActivities(0);
            stats.setActivitiesProgress(new ArrayList<>());
        }

        return stats;
    }

    public List<ActivityProgressDTO> getActivitiesByProject(Long projectId) {
        List<ActivityProgressDTO> activities = new ArrayList<>();

        try {
            // Get actives through AffectationEquipe
            List<Active> actives = projectProgressRepository.findActivesByProjectId(projectId);

            if (actives == null || actives.isEmpty()) {
                return activities;
            }

            // Get AffectationEquipe for all actives in this project
            List<Long> activeIds = actives.stream()
                    .map(Active::getId)
                    .collect(Collectors.toList());

            List<AffectationEquipe> affectationEquipes = projectProgressRepository.findAffectationEquipesByActiveIdsAndProject(activeIds, projectId);
            Map<Long, AffectationEquipe> affectationMap = affectationEquipes.stream()
                    .collect(Collectors.toMap(
                            ae -> ae.getActive().getId(),
                            ae -> ae,
                            (existing, replacement) -> existing
                    ));

            // Get all rendements for this project
            List<Rendement> rendements = projectProgressRepository.findRendementsByProjectId(projectId);

            // Group rendements by active
            Map<Long, List<Rendement>> rendementsByActive = new HashMap<>();
            if (rendements != null) {
                for (Rendement rend : rendements) {
                    if (rend.getAffectationEquipe() != null &&
                            rend.getAffectationEquipe().getActive() != null) {
                        Long activeId = rend.getAffectationEquipe().getActive().getId();
                        rendementsByActive.computeIfAbsent(activeId, k -> new ArrayList<>()).add(rend);
                    }
                }
            }

            for (Active active : actives) {
                ActivityProgressDTO dto = new ActivityProgressDTO();
                dto.setActiveId(active.getId());
                dto.setCodeActive(active.getCodeActive());
                dto.setObjectif(active.getObjectif());

                AffectationEquipe affectation = affectationMap.get(active.getId());
                String status = calculateStatusFromAffectation(affectation);
                dto.setStatus(status);

                // FIXED: Convert Double to int properly
                double progression = calculateProgressionFromStatus(status);
                dto.setCompletionPercentage((int) Math.round(progression));

                List<Rendement> activityRendements = rendementsByActive.getOrDefault(active.getId(), new ArrayList<>());
                double totalProductivity = 0;
                double totalHours = 0;

                for (Rendement rend : activityRendements) {
                    totalProductivity += rend.getValeurRendement() != null ? rend.getValeurRendement() : 0;
                    totalHours += rend.getDureeHeures() != null ? rend.getDureeHeures() : 0;
                }

                dto.setProductivityValue(totalProductivity);
                dto.setTotalWorkHours(totalHours);
                dto.setRendementCount(activityRendements.size());

                activities.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return activities;
    }

    /**
     * Get status for a specific activity
     */
    public String getActivityStatus(Long activeId, Long projectId) {
        try {
            List<AffectationEquipe> affectations = projectProgressRepository.findAffectationEquipesByActiveIdAndProject(activeId, projectId);
            if (affectations.isEmpty()) {
                return "PLANIFIER";
            }
            return calculateStatusFromAffectation(affectations.get(0));
        } catch (Exception e) {
            return "PLANIFIER";
        }
    }

    /**
     * Calculate project progression based on activities completion
     */
    public int calculateProjectProgressionFromActivities(Long projectId) {
        try {
            // Get activities through AffectationEquipe
            List<Active> actives = projectProgressRepository.findActivesByProjectId(projectId);

            if (actives == null || actives.isEmpty()) {
                return 0;
            }

            // Get AffectationEquipe for all actives
            List<Long> activeIds = actives.stream()
                    .map(Active::getId)
                    .collect(Collectors.toList());

            List<AffectationEquipe> affectationEquipes = projectProgressRepository.findAffectationEquipesByActiveIdsAndProject(activeIds, projectId);
            Map<Long, AffectationEquipe> affectationMap = affectationEquipes.stream()
                    .collect(Collectors.toMap(
                            ae -> ae.getActive().getId(),
                            ae -> ae,
                            (existing, replacement) -> existing
                    ));

            // Calculate total completion percentage from all activities
            double totalCompletion = 0;
            int activityCount = actives.size();

            for (Active active : actives) {
                AffectationEquipe affectation = affectationMap.get(active.getId());
                String status = calculateStatusFromAffectation(affectation);
                double progression = calculateProgressionFromStatus(status);
                totalCompletion += progression;
            }

            // FIXED: Convert Double to int properly
            return (int) Math.round(totalCompletion / activityCount);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}