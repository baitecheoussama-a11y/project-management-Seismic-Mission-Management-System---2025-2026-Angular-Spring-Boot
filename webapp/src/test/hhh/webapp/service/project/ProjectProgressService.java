package com.pfe.webapp.service.project;

import com.pfe.webapp.dto.project.ActivityProgressDTO;
import com.pfe.webapp.dto.project.ProjectProgressStatsDTO;
import com.pfe.webapp.entity.Active;
import com.pfe.webapp.entity.EtatAvancement;
import com.pfe.webapp.entity.Rapport;
import com.pfe.webapp.entity.Rendement;
import com.pfe.webapp.repository.project.ProjectProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProjectProgressService {

    @Autowired
    private ProjectProgressRepository projectProgressRepository;

    public ProjectProgressStatsDTO getProjectProgressStats(Long projectId) {
        ProjectProgressStatsDTO stats = new ProjectProgressStatsDTO();

        try {
            // 1. Get reports (Rapport -> Project)
            List<Rapport> rapports = projectProgressRepository.findRapportsByProjectId(projectId);
            stats.setTotalReports(rapports != null ? rapports.size() : 0);

            // 2. Get all Rendements (Rendement -> Rapport -> Project)
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

            // 4. Get actives from rendements (Rendement -> AffectationEquipe -> Active)
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

            // Get EtatAvancement for all actives
            List<Long> activeIds = actives.stream()
                    .map(Active::getId)
                    .collect(Collectors.toList());

            List<EtatAvancement> allEtatAvancements = projectProgressRepository.findEtatAvancementsByActiveIds(activeIds);
            Map<Long, EtatAvancement> etatAvancementMap = allEtatAvancements.stream()
                    .collect(Collectors.toMap(e -> e.getActive().getId(), e -> e));

            // Group rendements by active (from the rendements we already have)
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

                EtatAvancement etat = etatAvancementMap.get(active.getId());

                if (etat != null && etat.getStatus() != null) {
                    dto.setStatus(etat.getStatus().name());

                    switch (etat.getStatus()) {
                        case TERMINI:
                            completedCount++;
                            dto.setCompletionPercentage(100);
                            break;
                        case ENCOURS:
                            inProgressCount++;
                            dto.setCompletionPercentage(50);
                            break;
                        case ENATTENTE:
                            pendingCount++;
                            dto.setCompletionPercentage(25);
                            break;
                        case ENRETARD:
                            delayedCount++;
                            dto.setCompletionPercentage(40);
                            break;
                        default:
                            pendingCount++;
                            dto.setCompletionPercentage(0);
                    }
                } else {
                    pendingCount++;
                    dto.setStatus("PENDING");
                    dto.setCompletionPercentage(0);
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
            // Get actives from rendements
            List<Active> actives = projectProgressRepository.findActivesByProjectId(projectId);

            if (actives == null || actives.isEmpty()) {
                return activities;
            }

            // Get all rendements for this project
            List<Rendement> rendements = projectProgressRepository.findRendementsByProjectId(projectId);

            // Get EtatAvancement for all actives
            List<Long> activeIds = actives.stream()
                    .map(Active::getId)
                    .collect(Collectors.toList());

            List<EtatAvancement> allEtatAvancements = projectProgressRepository.findEtatAvancementsByActiveIds(activeIds);
            Map<Long, EtatAvancement> etatAvancementMap = allEtatAvancements.stream()
                    .collect(Collectors.toMap(e -> e.getActive().getId(), e -> e));

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

                EtatAvancement etat = etatAvancementMap.get(active.getId());
                if (etat != null && etat.getStatus() != null) {
                    dto.setStatus(etat.getStatus().name());
                    switch (etat.getStatus()) {
                        case TERMINI:
                            dto.setCompletionPercentage(100);
                            break;
                        case ENCOURS:
                            dto.setCompletionPercentage(50);
                            break;
                        case ENATTENTE:
                            dto.setCompletionPercentage(25);
                            break;
                        case ENRETARD:
                            dto.setCompletionPercentage(40);
                            break;
                        default:
                            dto.setCompletionPercentage(0);
                    }
                } else {
                    dto.setStatus("PENDING");
                    dto.setCompletionPercentage(0);
                }

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

    public Optional<EtatAvancement> getActivityProgress(Long activeId) {
        return projectProgressRepository.findEtatAvancementByActiveId(activeId);
    }

    // In ProjectProgressService.java - Add this method

    /**
     * Calculate project progression based on activities completion
     * @param projectId the project ID
     * @return progression percentage (0-100)
     */
    public int calculateProjectProgressionFromActivities(Long projectId) {
        try {
            // Get activities for this project
            List<Active> actives = projectProgressRepository.findActivesByProjectId(projectId);

            if (actives == null || actives.isEmpty()) {
                return 0;
            }

            // Get EtatAvancement for all actives
            List<Long> activeIds = actives.stream()
                    .map(Active::getId)
                    .collect(Collectors.toList());

            List<EtatAvancement> allEtatAvancements = projectProgressRepository.findEtatAvancementsByActiveIds(activeIds);
            Map<Long, EtatAvancement> etatAvancementMap = allEtatAvancements.stream()
                    .collect(Collectors.toMap(e -> e.getActive().getId(), e -> e));

            // Calculate total completion percentage from all activities
            double totalCompletion = 0;
            int activityCount = actives.size();

            for (Active active : actives) {
                EtatAvancement etat = etatAvancementMap.get(active.getId());
                int completionPercentage = 0;

                if (etat != null && etat.getStatus() != null) {
                    switch (etat.getStatus()) {
                        case TERMINI:
                            completionPercentage = 100;
                            break;
                        case ENCOURS:
                            completionPercentage = 50;
                            break;
                        case ENATTENTE:
                            completionPercentage = 25;
                            break;
                        case ENRETARD:
                            completionPercentage = 40;
                            break;
                        default:
                            completionPercentage = 0;
                    }
                }
                totalCompletion += completionPercentage;
            }

            // Average completion percentage
            return (int) Math.round(totalCompletion / activityCount);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}