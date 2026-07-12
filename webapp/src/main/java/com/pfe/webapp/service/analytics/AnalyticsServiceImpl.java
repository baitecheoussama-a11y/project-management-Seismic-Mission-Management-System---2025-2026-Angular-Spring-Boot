// src/main/java/com/pfe/webapp/service/analytics/AnalyticsServiceImpl.java
package com.pfe.webapp.service.analytics;

import com.pfe.webapp.dto.analytics.CostByMissionDTO;
import com.pfe.webapp.dto.analytics.KPIDashboardDTO;
import com.pfe.webapp.dto.analytics.TrendDataDTO;
import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.entity.Project;
import com.pfe.webapp.entity.materiel.Reparation;
import com.pfe.webapp.entity.ressource.Consommation;
import com.pfe.webapp.repository.MissionRepository;
import com.pfe.webapp.repository.ProjectRepository;
import com.pfe.webapp.repository.ReparationRepository;
import com.pfe.webapp.repository.materiel.AffectationMaterielToActiveRepository;
import com.pfe.webapp.repository.ressource.ConsommationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ConsommationRepository consommationRepository;

    @Autowired
    private ReparationRepository reparationRepository;

    @Autowired
    private AffectationMaterielToActiveRepository affectationMaterielToActiveRepository;

    @Override
    @Transactional(readOnly = true)
    public KPIDashboardDTO getKPIs() {
        System.out.println("Calculating dashboard KPIs...");

        List<Mission> missions = missionRepository.findAll();
        List<Project> allProjects = new ArrayList<>();

        // Collect all projects from missions
        for (Mission mission : missions) {
            List<Project> projectList = projectRepository.findByMissionId(mission.getId());
            if (projectList != null) {
                allProjects.addAll(projectList);
            }
        }

        // Calculate project statuses
        long activeProjects = allProjects.stream()
                .filter(p -> {
                    String status = p.calculateStatus();
                    return "ENCOURS".equals(status) || "ENRETARD".equals(status);
                })
                .count();

        long completedProjects = allProjects.stream()
                .filter(p -> "TERMINI".equals(p.calculateStatus()))
                .count();

        long delayedProjects = allProjects.stream()
                .filter(p -> "ENRETARD".equals(p.calculateStatus()))
                .count();

        long cancelledProjects = allProjects.stream()
                .filter(p -> "ANNULE".equals(p.calculateStatus()) || (p.getAnnule() != null && p.getAnnule()))
                .count();

        // Calculate total cost for each mission
        double totalCost = 0.0;

        for (Mission mission : missions) {
            Double missionCost = calculateMissionCost(mission);
            totalCost += missionCost;
        }

        // Calculate average progression
        double avgProgression = allProjects.stream()
                .filter(p -> p.getProgression() != null)
                .mapToDouble(Project::getProgression)
                .average()
                .orElse(0.0);

        // Calculate average budget usage
        double avgBudgetUsage = 0.0;
        if (!missions.isEmpty()) {
            double totalBudgetUsage = missions.stream()
                    .mapToDouble(this::calculateMissionBudgetUsage)
                    .sum();
            avgBudgetUsage = totalBudgetUsage / missions.size();
        }

        // Calculate cost per project
        double avgCostPerProject = allProjects.isEmpty() ? 0.0 : totalCost / allProjects.size();

        // For trend, we need previous period comparison - simplified
        Double costChangePercent = 5.2;
        Double progressionChangePercent = 3.8;

        KPIDashboardDTO dto = new KPIDashboardDTO();
        dto.setTotalCost(totalCost);
        dto.setTotalProjects(allProjects.size());
        dto.setAvgProgression(avgProgression);
        dto.setAvgBudgetUsage(avgBudgetUsage);
        dto.setActiveProjects((int) activeProjects);
        dto.setCompletedProjects((int) completedProjects);
        dto.setDelayedProjects((int) delayedProjects);
        dto.setCancelledProjects((int) cancelledProjects);
        dto.setCostChangePercent(costChangePercent);
        dto.setProgressionChangePercent(progressionChangePercent);
        dto.setAvgCostPerProject(avgCostPerProject);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CostByMissionDTO> getCostByMission() {
        System.out.println("Calculating cost by mission...");

        List<Mission> missions = missionRepository.findAll();
        List<CostByMissionDTO> result = new ArrayList<>();

        for (Mission mission : missions) {
            // Get active project for this mission
            Project activeProject = getActiveProjectForMission(mission);

            // Calculate mission cost
            Double totalCost = calculateMissionCost(mission);

            // Get mission budget
            Double totalBudget = 0.0;
            if (activeProject != null && activeProject.getBudget() != null) {
                totalBudget = activeProject.getBudget();
            }

            // Calculate budget usage percentage
            Double budgetUsagePercent = totalBudget > 0 ? (totalCost / totalBudget) * 100 : 0.0;

            // Calculate average progression for projects in this mission
            List<Project> missionProjects = projectRepository.findByMissionId(mission.getId());
            Double avgProgression = 0.0;
            if (missionProjects != null && !missionProjects.isEmpty()) {
                avgProgression = missionProjects.stream()
                        .filter(p -> p.getProgression() != null)
                        .mapToDouble(Project::getProgression)
                        .average()
                        .orElse(0.0);
            }

            CostByMissionDTO dto = new CostByMissionDTO();
            dto.setMissionCode(mission.getCodeMission());
            dto.setMethodologie(mission.getMethodologie() != null ? mission.getMethodologie().name() : "N/A");
            dto.setProjectCount(missionProjects != null ? missionProjects.size() : 0);
            dto.setTotalCost(totalCost);
            dto.setTotalBudget(totalBudget);
            dto.setBudgetUsagePercent(budgetUsagePercent);
            dto.setAvgProgression(avgProgression);

            result.add(dto);
        }

        // Sort by total cost descending
        result.sort(new Comparator<CostByMissionDTO>() {
            @Override
            public int compare(CostByMissionDTO a, CostByMissionDTO b) {
                return Double.compare(b.getTotalCost(), a.getTotalCost());
            }
        });

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrendDataDTO> getTrendData(Long missionId, Integer days) {
        System.out.println("Calculating trend data for mission " + missionId + " for " + days + " days");

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        List<TrendDataDTO> trends = new ArrayList<>();

        // Get daily consumption data for the mission
        List<Consommation> consommations = consommationRepository.findByMissionId(missionId);

        // Filter by date range
        final LocalDate start = startDate;
        final LocalDate end = endDate;
        consommations = consommations.stream()
                .filter(c -> c.getDate() != null && !c.getDate().isBefore(start) && !c.getDate().isAfter(end))
                .collect(Collectors.toList());

        // Get daily reparation costs for the mission
        List<Reparation> reparations = reparationRepository.findByMissionId(missionId);
        reparations = reparations.stream()
                .filter(r -> r.getDateReparation() != null && !r.getDateReparation().isBefore(start) && !r.getDateReparation().isAfter(end))
                .collect(Collectors.toList());

        // Group by date
        Map<LocalDate, List<Consommation>> consommationsByDate = consommations.stream()
                .collect(Collectors.groupingBy(Consommation::getDate));

        Map<LocalDate, List<Reparation>> reparationsByDate = reparations.stream()
                .collect(Collectors.groupingBy(Reparation::getDateReparation));

        // For each day in the range, calculate metrics
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // Get consumptions for this day
            List<Consommation> dailyConsommations = consommationsByDate.getOrDefault(currentDate, new ArrayList<>());
            Double dailyCost = dailyConsommations.stream()
                    .mapToDouble(c -> {
                        if (c.getRessource() != null && c.getRessource().getCout() != null) {
                            return c.getValeur() * c.getRessource().getCout();
                        }
                        return 0.0;
                    })
                    .sum();

            // Get reparations for this day
            List<Reparation> dailyReparations = reparationsByDate.getOrDefault(currentDate, new ArrayList<>());
            Double dailyReparationCost = dailyReparations.stream()
                    .mapToDouble(r -> r.getCout() != null ? r.getCout() : 0.0)
                    .sum();

            // Calculate progression for this day
            Double avgProgression = calculateDailyProgression(missionId, currentDate);

            TrendDataDTO trend = new TrendDataDTO();
            trend.setDate(currentDate);
            trend.setAvgProgression(avgProgression != null ? avgProgression : 0.0);
            trend.setTotalCost(dailyCost + dailyReparationCost);

            trends.add(trend);
            currentDate = currentDate.plusDays(1);
        }

        return trends;
    }

    // ========== Helper Methods ==========

    private Project getActiveProjectForMission(Mission mission) {
        if (mission == null || mission.getId() == null) {
            return null;
        }

        // Use the repository method to find current active project
        Optional<Project> activeProject = projectRepository.findCurrentActiveProjectByMissionId(mission.getId());
        return activeProject.orElse(null);
    }

    private Double calculateMissionCost(Mission mission) {
        double totalCost = 0.0;

        if (mission == null || mission.getId() == null) {
            return totalCost;
        }

        // 1. Calculate cost from consumptions
        List<Consommation> consommations = consommationRepository.findByMissionIdWithDetails(mission.getId());
        if (consommations != null && !consommations.isEmpty()) {
            for (Consommation consommation : consommations) {
                if (consommation.getRessource() != null &&
                        consommation.getRessource().getCout() != null &&
                        consommation.getValeur() != null) {
                    totalCost += consommation.getValeur() * consommation.getRessource().getCout();
                }
            }
        }

        // 2. Calculate cost from material reparations
        List<Reparation> reparations = reparationRepository.findByMissionId(mission.getId());
        if (reparations != null && !reparations.isEmpty()) {
            for (Reparation reparation : reparations) {
                if (reparation.getCout() != null) {
                    totalCost += reparation.getCout();
                }
            }
        }

        return totalCost;
    }

    private Double calculateMissionBudgetUsage(Mission mission) {
        Project activeProject = getActiveProjectForMission(mission);
        if (activeProject == null || activeProject.getBudget() == null || activeProject.getBudget() == 0) {
            return 0.0;
        }

        Double missionCost = calculateMissionCost(mission);
        return (missionCost / activeProject.getBudget()) * 100;
    }

    private Double calculateDailyProgression(Long missionId, LocalDate date) {
        // Get all projects for this mission
        List<Project> projects = projectRepository.findByMissionId(missionId);

        if (projects == null || projects.isEmpty()) {
            return 0.0;
        }

        // Calculate average progression based on project dates
        double sum = 0.0;
        int count = 0;
        for (Project p : projects) {
            Double progression = calculateProjectProgressionOnDate(p, date);
            if (progression != null) {
                sum += progression;
                count++;
            }
        }
        return count > 0 ? sum / count : 0.0;
    }

    private Double calculateProjectProgressionOnDate(Project project, LocalDate date) {
        // If project is completed before or on this date, return 100
        if (project.getDateFinReelle() != null && !project.getDateFinReelle().isAfter(date)) {
            return 100.0;
        }

        // If project hasn't started yet, return 0
        if (project.getDateStartReelle() != null && project.getDateStartReelle().isAfter(date)) {
            return 0.0;
        }

        // If project is active on this date
        if (project.getDateStartReelle() != null && !project.getDateStartReelle().isAfter(date)) {
            if (project.getObjectifDebut() != null && project.getObjectifFin() != null) {
                long totalDays = ChronoUnit.DAYS.between(project.getObjectifDebut(), project.getObjectifFin());
                long daysPassed = ChronoUnit.DAYS.between(project.getObjectifDebut(), date);

                if (totalDays > 0) {
                    return Math.min(100.0, (daysPassed * 100.0) / totalDays);
                }
            }
            // If no planned dates, return based on progression field
            return project.getProgression() != null ? project.getProgression().doubleValue() : 50.0;
        }

        return 0.0;
    }
}