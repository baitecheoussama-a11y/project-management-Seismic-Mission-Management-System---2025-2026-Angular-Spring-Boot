package com.pfe.webapp.controller.stats;

import com.pfe.webapp.dto.stats.*;
import com.pfe.webapp.service.stats.ProductionStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats/production")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductionStatsController {

    @Autowired
    private ProductionStatsService productionStatsService;

    // ========== COMPLETE SUMMARY ==========

    @GetMapping("/summary")
    public ResponseEntity<ProductionStatsDTO> getProductionStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(productionStatsService.getProductionStats(startDate, endDate));
    }

    // ========== KPI ENDPOINTS ==========

    @GetMapping("/kpi/total-records")
    public ResponseEntity<Long> getTotalProductionRecords() {
        return ResponseEntity.ok(productionStatsService.getTotalProductionRecords());
    }

    @GetMapping("/kpi/average-productivity")
    public ResponseEntity<Double> getAverageProductivity() {
        return ResponseEntity.ok(productionStatsService.getAverageProductivity());
    }

    @GetMapping("/kpi/active-teams")
    public ResponseEntity<Long> getActiveTeamsCount() {
        return ResponseEntity.ok(productionStatsService.getActiveTeamsCount());
    }

    @GetMapping("/kpi/avg-duration")
    public ResponseEntity<Double> getAverageActivityDuration() {
        return ResponseEntity.ok(productionStatsService.getAverageActivityDuration());
    }

    // ========== CHART ENDPOINTS ==========

    @GetMapping("/by-team")
    public ResponseEntity<List<ProductionByTeamDTO>> getProductionByTeam(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(productionStatsService.getProductionByTeam(startDate, endDate));
    }

    @GetMapping("/by-activity")
    public ResponseEntity<List<ProductionByActivityDTO>> getProductionByActivity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(productionStatsService.getProductionByActivity(startDate, endDate));
    }

    @GetMapping("/trend")
    public ResponseEntity<List<ProductionTrendDTO>> getProductionTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(productionStatsService.getProductionTrend(startDate, endDate));
    }

    @GetMapping("/by-mission")
    public ResponseEntity<List<ProductionByMissionDTO>> getProductionByMission(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(productionStatsService.getProductionByMission(startDate, endDate));
    }

    // ========== TABLE ENDPOINTS ==========

    @GetMapping("/top5/teams")
    public ResponseEntity<List<TopTeamDTO>> getTop5Teams() {
        return ResponseEntity.ok(productionStatsService.getTop5Teams());
    }

    @GetMapping("/top5/activities")
    public ResponseEntity<List<TopActivityDTO>> getTop5Activities() {
        return ResponseEntity.ok(productionStatsService.getTop5Activities());
    }

    // ========== ADVANCED ENDPOINTS ==========

    @GetMapping("/productivity/teams")
    public ResponseEntity<List<ProductivityByTeamDTO>> getProductivityByTeam() {
        return ResponseEntity.ok(productionStatsService.getProductivityByTeam());
    }

    @GetMapping("/productivity/activities")
    public ResponseEntity<List<ProductivityByActivityDTO>> getProductivityByActivity() {
        return ResponseEntity.ok(productionStatsService.getProductivityByActivity());
    }

    // ========== FILTER BY MISSION ==========

    @GetMapping("/by-team/mission/{missionId}")
    public ResponseEntity<List<ProductionByTeamDTO>> getProductionByTeamByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(productionStatsService.getProductionByTeamByMission(missionId));
    }

    @GetMapping("/by-activity/mission/{missionId}")
    public ResponseEntity<List<ProductionByActivityDTO>> getProductionByActivityByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(productionStatsService.getProductionByActivityByMission(missionId));
    }
}