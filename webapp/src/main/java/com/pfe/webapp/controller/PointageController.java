package com.pfe.webapp.controller;

import com.pfe.webapp.dto.pointage.PointageRequestDTO;
import com.pfe.webapp.dto.pointage.PointageResponseDTO;
import com.pfe.webapp.dto.pointage.PointageStatsDTO;
import com.pfe.webapp.entity.StatusPointage;
import com.pfe.webapp.service.pointage.PointageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/pointages")
@CrossOrigin(origins = "http://localhost:4200")
public class PointageController {

    @Autowired
    private PointageService pointageService;

    // ========== GET METHODS ==========

    @GetMapping("/date/{date}")
    public ResponseEntity<List<PointageResponseDTO>> getPointagesByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(pointageService.getPointagesByDate(date));
    }

    @GetMapping("/range")
    public ResponseEntity<List<PointageResponseDTO>> getPointagesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(pointageService.getPointagesByDateRange(start, end));
    }

    @GetMapping("/employe/{employeId}")
    public ResponseEntity<List<PointageResponseDTO>> getPointagesByEmploye(@PathVariable Long employeId) {
        return ResponseEntity.ok(pointageService.getPointagesByEmploye(employeId));
    }

    @GetMapping("/employe/{employeId}/date/{date}")
    public ResponseEntity<PointageResponseDTO> getPointageByEmployeAndDate(
            @PathVariable Long employeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(pointageService.getPointageByEmployeAndDate(employeId, date));
    }

    @GetMapping("/today")
    public ResponseEntity<List<PointageResponseDTO>> getTodaysPointages() {
        return ResponseEntity.ok(pointageService.getTodaysPointages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointageResponseDTO> getPointageById(@PathVariable Long id) {
        return ResponseEntity.ok(pointageService.getPointageById(id));
    }

    // ========== STATS METHODS ==========

    @GetMapping("/date/{date}/stats")
    public ResponseEntity<PointageStatsDTO> getStatsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(pointageService.getStatsByDate(date));
    }

    @GetMapping("/today/stats")
    public ResponseEntity<PointageStatsDTO> getTodaysStats() {
        return ResponseEntity.ok(pointageService.getStatsByDate(LocalDate.now()));
    }

    // ========== STATUS OPTIONS ==========

    @GetMapping("/statuses")
    public ResponseEntity<List<StatusPointage>> getStatusOptions() {
        return ResponseEntity.ok(pointageService.getStatusOptions());
    }

    // ========== CREATE, UPDATE, DELETE ==========

    @PostMapping
    public ResponseEntity<PointageResponseDTO> createPointage(@RequestBody PointageRequestDTO request) {
        PointageResponseDTO created = pointageService.createPointage(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PointageResponseDTO> updatePointage(
            @PathVariable Long id,
            @RequestBody PointageRequestDTO request) {
        PointageResponseDTO updated = pointageService.updatePointage(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePointage(@PathVariable Long id) {
        pointageService.deletePointage(id);
        return ResponseEntity.noContent().build();
    }

    // ========== BATCH OPERATIONS ==========

    @PostMapping("/mark-all-present")
    public ResponseEntity<Void> markAllPresent(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        pointageService.markAllPresent(date);
        return ResponseEntity.ok().build();
    }
}