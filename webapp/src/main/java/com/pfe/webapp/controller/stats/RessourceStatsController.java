package com.pfe.webapp.controller.stats;

import com.pfe.webapp.dto.stats.*;
import com.pfe.webapp.service.stats.RessourceStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats/ressources")
@CrossOrigin(origins = "http://localhost:4200")
public class RessourceStatsController {

    @Autowired
    private RessourceStatsService ressourceStatsService;

    // ========== COMPLETE SUMMARY ==========

    @GetMapping("/summary")
    public ResponseEntity<RessourceStatsSummaryDTO> getStatsSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ressourceStatsService.getStatsSummary(startDate, endDate));
    }

    // ========== INDIVIDUAL ENDPOINTS ==========

    @GetMapping("/by-ressource")
    public ResponseEntity<List<ConsommationStatsDTO>> getConsommationByRessource(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ressourceStatsService.getConsommationByRessource(startDate, endDate));
    }

    @GetMapping("/by-mission")
    public ResponseEntity<List<ConsommationStatsDTO>> getConsommationByMission(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ressourceStatsService.getConsommationByMission(startDate, endDate));
    }

    @GetMapping("/cost-by-ressource")
    public ResponseEntity<List<RessourceCostStatsDTO>> getCostByRessource(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ressourceStatsService.getCostByRessource(startDate, endDate));
    }

    @GetMapping("/by-month")
    public ResponseEntity<List<MonthlyConsommationDTO>> getConsommationByMonth(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ressourceStatsService.getConsommationByMonth(startDate, endDate));
    }

    @GetMapping("/total-cost")
    public ResponseEntity<Double> getTotalCost(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ressourceStatsService.getTotalCost(startDate, endDate));
    }

    @GetMapping("/by-type")
    public ResponseEntity<List<ConsommationStatsDTO>> getConsommationByType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ressourceStatsService.getConsommationByType(startDate, endDate));
    }

    @GetMapping("/cost-by-type")
    public ResponseEntity<List<RessourceCostStatsDTO>> getCostByType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ressourceStatsService.getCostByType(startDate, endDate));
    }

    @GetMapping("/top5")
    public ResponseEntity<List<RessourceCostStatsDTO>> getTop5Ressources(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ressourceStatsService.getTop5Ressources(startDate, endDate));
    }

    @GetMapping("/critical-stock")
    public ResponseEntity<List<RessourceCostStatsDTO>> getCriticalStock() {
        return ResponseEntity.ok(ressourceStatsService.getCriticalStock());
    }
}