package com.pfe.webapp.controller;

import com.pfe.webapp.dto.event.EvenementRequestDTO;
import com.pfe.webapp.dto.event.EvenementResponseDTO;
import com.pfe.webapp.service.EvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/evenements")
@CrossOrigin(origins = "http://localhost:4200")

public class EvenementController {

    @Autowired
    private EvenementService evenementService;

    // ========== CREATE ==========
    @PostMapping
    public ResponseEntity<EvenementResponseDTO> createEvenement(@RequestBody EvenementRequestDTO requestDTO) {
        try {
            EvenementResponseDTO createdEvenement = evenementService.createEvenement(requestDTO);
            return new ResponseEntity<>(createdEvenement, HttpStatus.CREATED);
        } catch (Exception e) {
            // الطريقة الصحيحة 1
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            // أو الطريقة الصحيحة 2
            // return ResponseEntity.badRequest().build();
        }
    }

    // ========== UPDATE ==========
    @PutMapping("/{id}")
    public ResponseEntity<EvenementResponseDTO> updateEvenement(@PathVariable Long id,
                                                                @RequestBody EvenementRequestDTO requestDTO) {
        try {
            EvenementResponseDTO updatedEvenement = evenementService.updateEvenement(id, requestDTO);
            return ResponseEntity.ok(updatedEvenement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // ========== DELETE ==========
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        try {
            evenementService.deleteEvenement(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== GET BY ID ==========
    @GetMapping("/{id}")
    public ResponseEntity<EvenementResponseDTO> getEvenementById(@PathVariable Long id) {
        try {
            EvenementResponseDTO evenement = evenementService.getEvenementById(id);
            return ResponseEntity.ok(evenement);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== GET ALL ==========
    @GetMapping
    public ResponseEntity<List<EvenementResponseDTO>> getAllEvenements() {
        List<EvenementResponseDTO> evenements = evenementService.getAllEvenements();
        return ResponseEntity.ok(evenements);
    }

    // ========== GET BY MISSION ==========
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<EvenementResponseDTO>> getEvenementsByMission(@PathVariable Long missionId) {
        List<EvenementResponseDTO> evenements = evenementService.getEvenementsByMission(missionId);
        return ResponseEntity.ok(evenements);
    }

    // ========== GET BY TYPE ==========
    @GetMapping("/type/{typeEvenementId}")
    public ResponseEntity<List<EvenementResponseDTO>> getEvenementsByType(@PathVariable Long typeEvenementId) {
        List<EvenementResponseDTO> evenements = evenementService.getEvenementsByType(typeEvenementId);
        return ResponseEntity.ok(evenements);
    }

    // ========== GET BY DATE ==========
    @GetMapping("/date")
    public ResponseEntity<List<EvenementResponseDTO>> getEvenementsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<EvenementResponseDTO> evenements = evenementService.getEvenementsByDate(date);
        return ResponseEntity.ok(evenements);
    }

    // ========== GET BY DATE RANGE ==========
    @GetMapping("/date-range")
    public ResponseEntity<List<EvenementResponseDTO>> getEvenementsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<EvenementResponseDTO> evenements = evenementService.getEvenementsByDateRange(startDate, endDate);
        return ResponseEntity.ok(evenements);
    }

    // ========== GET UPCOMING ==========
    @GetMapping("/upcoming")
    public ResponseEntity<List<EvenementResponseDTO>> getUpcomingEvenements() {
        List<EvenementResponseDTO> evenements = evenementService.getUpcomingEvenements();
        return ResponseEntity.ok(evenements);
    }

    // ========== GET PAST ==========
    @GetMapping("/past")
    public ResponseEntity<List<EvenementResponseDTO>> getPastEvenements() {
        List<EvenementResponseDTO> evenements = evenementService.getPastEvenements();
        return ResponseEntity.ok(evenements);
    }

    // ========== GET TODAY'S EVENTS ==========
    @GetMapping("/today")
    public ResponseEntity<List<EvenementResponseDTO>> getTodaysEvenements() {
        List<EvenementResponseDTO> evenements = evenementService.getTodaysEvenements();
        return ResponseEntity.ok(evenements);
    }

    // ========== SEARCH ==========
    @GetMapping("/search")
    public ResponseEntity<List<EvenementResponseDTO>> searchEvenements(
            @RequestParam String keyword) {
        List<EvenementResponseDTO> evenements = evenementService.searchEvenements(keyword);
        return ResponseEntity.ok(evenements);
    }

    // ========== GET COUNT ==========
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalEvenementsCount() {
        long count = evenementService.getTotalEvenementsCount();
        return ResponseEntity.ok(count);
    }
}