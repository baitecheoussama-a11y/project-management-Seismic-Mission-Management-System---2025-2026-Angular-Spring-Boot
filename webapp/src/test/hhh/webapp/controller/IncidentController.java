package com.pfe.webapp.controller;

import com.pfe.webapp.dto.incident.IncidentRequestDTO;
import com.pfe.webapp.dto.incident.IncidentResponseDTO;
import com.pfe.webapp.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incidents")
@CrossOrigin(origins = "http://localhost:4200")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    // ========== CREATE ==========
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'Gestionnaire', 'CHEF_TERRAIN')")
    public ResponseEntity<IncidentResponseDTO> createIncident(@RequestBody IncidentRequestDTO requestDTO) {
        try {
            IncidentResponseDTO created = incidentService.createIncident(requestDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== UPDATE ==========
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'Gestionnaire', 'CHEF_TERRAIN')")
    public ResponseEntity<IncidentResponseDTO> updateIncident(@PathVariable Long id,
                                                              @RequestBody IncidentRequestDTO requestDTO) {
        try {
            IncidentResponseDTO updated = incidentService.updateIncident(id, requestDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== DELETE ==========
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'Gestionnaire')")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        try {
            incidentService.deleteIncident(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== GET BY ID ==========
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<IncidentResponseDTO> getIncidentById(@PathVariable Long id) {
        try {
            IncidentResponseDTO incident = incidentService.getIncidentById(id);
            return ResponseEntity.ok(incident);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== GET ALL ==========
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<IncidentResponseDTO>> getAllIncidents() {
        List<IncidentResponseDTO> incidents = incidentService.getAllIncidents();
        return ResponseEntity.ok(incidents);
    }

    // ========== GET ALL WITH PAGINATION ==========
    @GetMapping("/paged")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<IncidentResponseDTO>> getAllIncidentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateIncident") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<IncidentResponseDTO> incidents = incidentService.getAllIncidents(pageable);
        return ResponseEntity.ok(incidents);
    }

    // ========== GET BY EMPLOYE ==========
    @GetMapping("/employe/{employeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<IncidentResponseDTO>> getIncidentsByEmploye(@PathVariable Long employeId) {
        List<IncidentResponseDTO> incidents = incidentService.getIncidentsByEmploye(employeId);
        return ResponseEntity.ok(incidents);
    }

    // ========== GET BY TYPE ==========
    @GetMapping("/type/{type}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<IncidentResponseDTO>> getIncidentsByType(@PathVariable String type) {
        List<IncidentResponseDTO> incidents = incidentService.getIncidentsByType(type);
        return ResponseEntity.ok(incidents);
    }

    // ========== GET BY GRAVITE ==========
    @GetMapping("/gravite/{niveauGravite}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<IncidentResponseDTO>> getIncidentsByGravite(@PathVariable String niveauGravite) {
        List<IncidentResponseDTO> incidents = incidentService.getIncidentsByGravite(niveauGravite);
        return ResponseEntity.ok(incidents);
    }

    // ========== GET BY DATE ==========
    @GetMapping("/date")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<IncidentResponseDTO>> getIncidentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<IncidentResponseDTO> incidents = incidentService.getIncidentsByDate(date);
        return ResponseEntity.ok(incidents);
    }

    // ========== GET BY DATE RANGE ==========
    @GetMapping("/date-range")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<IncidentResponseDTO>> getIncidentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<IncidentResponseDTO> incidents = incidentService.getIncidentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(incidents);
    }

    // ========== GET RECENT ==========
    @GetMapping("/recent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<IncidentResponseDTO>> getRecentIncidents() {
        List<IncidentResponseDTO> incidents = incidentService.getRecentIncidents();
        return ResponseEntity.ok(incidents);
    }

    // ========== SEARCH ==========
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<IncidentResponseDTO>> searchIncidents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<IncidentResponseDTO> incidents = incidentService.searchIncidents(keyword, pageable);
        return ResponseEntity.ok(incidents);
    }

    // ========== GET COUNT ==========
    @GetMapping("/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getIncidentsCount() {
        long count = incidentService.getIncidentsCount();
        return ResponseEntity.ok(count);
    }

    // ========== STATISTICS ==========
    @GetMapping("/stats/by-type")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getIncidentsCountByType() {
        return ResponseEntity.ok(incidentService.getIncidentsCountByType());
    }

    @GetMapping("/stats/by-gravite")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getIncidentsCountByGravite() {
        return ResponseEntity.ok(incidentService.getIncidentsCountByGravite());
    }
    // IncidentController.java - أضف هذه الـ endpoints

    // ========== GET BY MISSION ==========
    @GetMapping("/mission/{missionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<IncidentResponseDTO>> getIncidentsByMission(@PathVariable Long missionId) {
        List<IncidentResponseDTO> incidents = incidentService.getIncidentsByMission(missionId);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/mission/{missionId}/paged")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<IncidentResponseDTO>> getIncidentsByMissionPaged(
            @PathVariable Long missionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dateIncident") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<IncidentResponseDTO> incidents = incidentService.getIncidentsByMission(missionId, pageable);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/mission/{missionId}/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<IncidentResponseDTO>> searchIncidentsByMission(
            @PathVariable Long missionId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<IncidentResponseDTO> incidents = incidentService.searchIncidentsByMission(missionId, keyword, pageable);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/mission/{missionId}/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getIncidentsStatsByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(incidentService.getIncidentsStatsByMission(missionId));
    }
}