// controller/EtatAvancementController.java
package com.pfe.webapp.controller.av;

import com.pfe.webapp.dto.*;
import com.pfe.webapp.dto.av.AvancementDTO;
import com.pfe.webapp.dto.av.AvancementRequestDTO;
import com.pfe.webapp.dto.av.EtatAvancementDTO;
import com.pfe.webapp.dto.av.UpdateStatusRequestDTO;
import com.pfe.webapp.service.av.EtatAvancementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etat-avancement")
@CrossOrigin(origins = "http://localhost:4200")
public class EtatAvancementController {

    @Autowired
    private EtatAvancementService etatAvancementService;

    // ============ ETAT AVANCEMENT ENDPOINTS ============

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<EtatAvancementDTO>> getEtatAvancementsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(etatAvancementService.getEtatAvancementsByProject(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EtatAvancementDTO> getEtatAvancementById(@PathVariable Long id) {
        return ResponseEntity.ok(etatAvancementService.getEtatAvancementById(id));
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<EtatAvancementDTO> createEtatAvancementForProject(@PathVariable Long projectId) {
        EtatAvancementDTO created = etatAvancementService.createEtatAvancementForProject(projectId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/active/{activeId}/project/{projectId}")
    public ResponseEntity<EtatAvancementDTO> createEtatAvancementForActive(
            @PathVariable Long activeId,
            @PathVariable Long projectId) {
        EtatAvancementDTO created = etatAvancementService.createEtatAvancementForActive(activeId, projectId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EtatAvancementDTO> updateEtatAvancementStatus(
            @PathVariable Long id,
            @RequestBody String status) {  // ✅ استقبل String مباشرة
        EtatAvancementDTO updated = etatAvancementService.updateEtatAvancementStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtatAvancement(@PathVariable Long id) {
        etatAvancementService.deleteEtatAvancement(id);
        return ResponseEntity.noContent().build();
    }

    // ============ AVANCEMENT ENDPOINTS ============

    @PostMapping("/{etatAvancementId}/avancements")
    public ResponseEntity<AvancementDTO> addAvancement(
            @PathVariable Long etatAvancementId,
            @RequestBody AvancementRequestDTO request) {
        AvancementDTO created = etatAvancementService.addAvancement(etatAvancementId, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{etatAvancementId}/avancements")
    public ResponseEntity<List<AvancementDTO>> getAvancementsByEtatAvancement(@PathVariable Long etatAvancementId) {
        return ResponseEntity.ok(etatAvancementService.getAvancementsByEtatAvancement(etatAvancementId));
    }

    @PutMapping("/avancements/{id}")
    public ResponseEntity<AvancementDTO> updateAvancement(
            @PathVariable Long id,
            @RequestBody AvancementRequestDTO request) {
        return ResponseEntity.ok(etatAvancementService.updateAvancement(id, request));
    }

    @DeleteMapping("/avancements/{id}")
    public ResponseEntity<Void> deleteAvancement(@PathVariable Long id) {
        etatAvancementService.deleteAvancement(id);
        return ResponseEntity.noContent().build();
    }
    // controller/av/EtatAvancementController.java - أضف هذه الـ endpoints
















    // Add these methods to your EtatAvancementController.java

    @GetMapping("/active/{activeId}/mission/{missionId}")
    public ResponseEntity<EtatAvancementDTO> getEtatAvancementByActiveAndMission(
            @PathVariable Long activeId,
            @PathVariable Long missionId) {
        EtatAvancementDTO etat = etatAvancementService.getEtatAvancementByActiveAndMission(activeId, missionId);
        if (etat == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(etat);
    }

    @PostMapping("/active/{activeId}/mission/{missionId}")
    public ResponseEntity<EtatAvancementDTO> createEtatAvancementForActiveAndMission(
            @PathVariable Long activeId,
            @PathVariable Long missionId) {
        EtatAvancementDTO created = etatAvancementService.createEtatAvancementForActiveAndMission(activeId, missionId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}