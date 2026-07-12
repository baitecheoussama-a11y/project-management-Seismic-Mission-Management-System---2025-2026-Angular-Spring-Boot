package com.pfe.webapp.controller;

import com.pfe.webapp.dto.*;
import com.pfe.webapp.entity.materiel.Reparation;
import com.pfe.webapp.entity.materiel.ReparationExterne;
import com.pfe.webapp.entity.materiel.ReparationInterne;
import com.pfe.webapp.service.ReparationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reparations")
@CrossOrigin(origins = "http://localhost:4200")
public class ReparationController {

    @Autowired
    private ReparationService reparationService;

    // ==================== DECLARE BREAKDOWN ====================

    @PostMapping("/panne")
    public ResponseEntity<Reparation> declarePanne(@RequestBody PanneRequestDTO request) {
        Reparation reparation = reparationService.declarePanne(request);
        return new ResponseEntity<>(reparation, HttpStatus.CREATED);
    }

    // ==================== LAUNCH REPAIR ====================

    @PostMapping("/launch")
    public ResponseEntity<Reparation> launchRepair(@RequestBody LancementReparationDTO request) {
        Reparation reparation = reparationService.launchRepair(request);
        return ResponseEntity.ok(reparation);
    }

    // ==================== COMPLETE REPAIR ====================

    @PostMapping("/complete")
    public ResponseEntity<Reparation> completeRepair(@RequestBody FinReparationDTO request) {
        Reparation reparation = reparationService.completeRepair(request);
        return ResponseEntity.ok(reparation);
    }

    // ==================== GET REPAIRS BY MATERIEL ====================

    @GetMapping("/materiel/{materielId}")
    public ResponseEntity<List<Reparation>> getAllByMateriel(@PathVariable Long materielId) {
        List<Reparation> reparations = reparationService.getAllReparationsByMateriel(materielId);
        return ResponseEntity.ok(reparations);
    }

    @GetMapping("/materiel/{materielId}/pending")
    public ResponseEntity<List<Reparation>> getPendingReparations(@PathVariable Long materielId) {
        List<Reparation> reparations = reparationService.getPendingReparations(materielId);
        return ResponseEntity.ok(reparations);
    }

    @GetMapping("/materiel/{materielId}/ongoing/internal")
    public ResponseEntity<List<ReparationInterne>> getOngoingInternalRepairs(@PathVariable Long materielId) {
        List<ReparationInterne> reparations = reparationService.getOngoingInternalRepairs(materielId);
        return ResponseEntity.ok(reparations);
    }

    @GetMapping("/materiel/{materielId}/ongoing/external")
    public ResponseEntity<List<ReparationExterne>> getOngoingExternalRepairs(@PathVariable Long materielId) {
        List<ReparationExterne> reparations = reparationService.getOngoingExternalRepairs(materielId);
        return ResponseEntity.ok(reparations);
    }

    @GetMapping("/materiel/{materielId}/completed")
    public ResponseEntity<List<Reparation>> getCompletedReparations(@PathVariable Long materielId) {
        List<Reparation> reparations = reparationService.getCompletedReparations(materielId);
        return ResponseEntity.ok(reparations);
    }

    // ==================== GET SINGLE REPAIR ====================

    @GetMapping("/{id}")
    public ResponseEntity<Reparation> getById(@PathVariable Long id) {
        Reparation reparation = reparationService.getReparationById(id);
        return ResponseEntity.ok(reparation);
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReparation(@PathVariable Long id) {
        reparationService.deleteReparation(id);
        return ResponseEntity.noContent().build();
    }
    // ==================== UPDATE ENDPOINTS ====================

    // ✅ Update pending breakdown
    @PutMapping("/{id}/panne")
    public ResponseEntity<Reparation> updatePanne(
            @PathVariable Long id,
            @RequestBody UpdatePanneRequestDTO request) {
        Reparation updated = reparationService.updatePanne(id, request);
        return ResponseEntity.ok(updated);
    }

    // ✅ Update internal repair
    @PutMapping("/{id}/internal")
    public ResponseEntity<ReparationInterne> updateInternalRepair(
            @PathVariable Long id,
            @RequestBody UpdateInternalRepairRequestDTO request) {
        ReparationInterne updated = reparationService.updateInternalRepair(id, request);
        return ResponseEntity.ok(updated);
    }

    // ✅ Update external repair
    @PutMapping("/{id}/external")
    public ResponseEntity<ReparationExterne> updateExternalRepair(
            @PathVariable Long id,
            @RequestBody UpdateExternalRepairRequestDTO request) {
        ReparationExterne updated = reparationService.updateExternalRepair(id, request);
        return ResponseEntity.ok(updated);
    }

    // ==================== GET REPAIRS BY MATERIEL AND MISSION ====================

    // ✅ Get all repairs (including completed) by materiel and mission
    @GetMapping("/materiel/{materielId}/mission/{missionId}/all")
    public ResponseEntity<List<Reparation>> getAllByMaterielAndMission(
            @PathVariable Long materielId,
            @PathVariable Long missionId) {
        List<Reparation> reparations = reparationService.getAllByMaterielAndMission(materielId, missionId);
        return ResponseEntity.ok(reparations);
    }

    @GetMapping("/materiel/{materielId}/mission/{missionId}/pending")
    public ResponseEntity<List<Reparation>> getPendingByMaterielAndMission(
            @PathVariable Long materielId,
            @PathVariable Long missionId) {
        List<Reparation> reparations = reparationService.getPendingReparationsByMaterielAndMission(materielId, missionId);
        return ResponseEntity.ok(reparations);
    }

    @GetMapping("/materiel/{materielId}/mission/{missionId}/ongoing")
    public ResponseEntity<List<Reparation>> getOngoingByMaterielAndMission(
            @PathVariable Long materielId,
            @PathVariable Long missionId) {
        List<Reparation> reparations = reparationService.getOngoingReparationsByMaterielAndMission(materielId, missionId);
        return ResponseEntity.ok(reparations);
    }

    @GetMapping("/materiel/{materielId}/mission/{missionId}/completed")
    public ResponseEntity<List<Reparation>> getCompletedByMaterielAndMission(
            @PathVariable Long materielId,
            @PathVariable Long missionId) {
        List<Reparation> reparations = reparationService.getCompletedReparationsByMaterielAndMission(materielId, missionId);
        return ResponseEntity.ok(reparations);
    }



}