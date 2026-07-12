package com.pfe.webapp.service.stats;

import com.pfe.webapp.dto.stats.*;
import com.pfe.webapp.repository.rapport.RendementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductionStatsService {

    @Autowired
    private RendementRepository rendementRepository;

    // ========== COMPLETE SUMMARY ==========

    public ProductionStatsDTO getProductionStats(LocalDate startDate, LocalDate endDate) {
        ProductionStatsDTO stats = new ProductionStatsDTO();

        // KPI Cards
        stats.setTotalProductionRecords(rendementRepository.countTotalProductionRecords());
        stats.setAverageProductivity(rendementRepository.getAverageProductivity());
        stats.setActiveTeamsCount(rendementRepository.countActiveTeams());
        stats.setAverageActivityDuration(rendementRepository.getAverageActivityDuration());

        // Charts with date filter
        stats.setProductionByTeam(rendementRepository.getProductionByTeam(startDate, endDate));
        stats.setProductionByActivity(rendementRepository.getProductionByActivity(startDate, endDate));
        stats.setProductionTrend(rendementRepository.getProductionTrend(startDate, endDate));
        stats.setProductionByMission(rendementRepository.getProductionByMission(startDate, endDate));

        // Top 5
        stats.setTop5Teams(getTop5Teams());
        stats.setTop5Activities(getTop5Activities());

        // Advanced (no date filter needed for these)
        stats.setProductivityByTeam(rendementRepository.getProductivityByTeam());
        stats.setProductivityByActivity(rendementRepository.getProductivityByActivity());

        return stats;
    }

    // ========== KPI METHODS ==========

    public Long getTotalProductionRecords() {
        return rendementRepository.countTotalProductionRecords();
    }

    public Double getAverageProductivity() {
        return rendementRepository.getAverageProductivity();
    }

    public Long getActiveTeamsCount() {
        return rendementRepository.countActiveTeams();
    }

    public Double getAverageActivityDuration() {
        return rendementRepository.getAverageActivityDuration();
    }

    // ========== CHART METHODS ==========

    public List<ProductionByTeamDTO> getProductionByTeam(LocalDate startDate, LocalDate endDate) {
        return rendementRepository.getProductionByTeam(startDate, endDate);
    }

    public List<ProductionByActivityDTO> getProductionByActivity(LocalDate startDate, LocalDate endDate) {
        return rendementRepository.getProductionByActivity(startDate, endDate);
    }

    public List<ProductionTrendDTO> getProductionTrend(LocalDate startDate, LocalDate endDate) {
        return rendementRepository.getProductionTrend(startDate, endDate);
    }

    public List<ProductionByMissionDTO> getProductionByMission(LocalDate startDate, LocalDate endDate) {
        return rendementRepository.getProductionByMission(startDate, endDate);
    }

    // ========== TABLE METHODS ==========

    public List<TopTeamDTO> getTop5Teams() {
        List<TopTeamDTO> teams = rendementRepository.getTop5Teams();
        // Add rank manually
        for (int i = 0; i < teams.size(); i++) {
            teams.get(i).setRank(i + 1);
        }
        return teams;
    }

    public List<TopActivityDTO> getTop5Activities() {
        List<TopActivityDTO> activities = rendementRepository.getTop5Activities();
        // Add rank manually
        for (int i = 0; i < activities.size(); i++) {
            activities.get(i).setRank(i + 1);
        }
        return activities;
    }
    // ========== ADVANCED METHODS ==========

    public List<ProductivityByTeamDTO> getProductivityByTeam() {
        return rendementRepository.getProductivityByTeam();
    }

    public List<ProductivityByActivityDTO> getProductivityByActivity() {
        return rendementRepository.getProductivityByActivity();
    }

    // ========== FILTER BY MISSION ==========

    public List<ProductionByTeamDTO> getProductionByTeamByMission(Long missionId) {
        return rendementRepository.getProductionByTeamByMission(missionId);
    }

    public List<ProductionByActivityDTO> getProductionByActivityByMission(Long missionId) {
        return rendementRepository.getProductionByActivityByMission(missionId);
    }
}