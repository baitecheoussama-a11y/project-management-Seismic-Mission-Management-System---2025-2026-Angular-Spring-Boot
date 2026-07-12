package com.pfe.webapp.controller;

import com.pfe.webapp.dto.AffectationMaterielDTO;
import com.pfe.webapp.dto.BatchAffectationRequestDTO;
import com.pfe.webapp.service.AffectationMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/affectations-materiel")
@CrossOrigin(origins = "http://localhost:4200")
public class AffectationMaterielController {

    @Autowired
    private AffectationMaterielService affectationService;

    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @RequestParam Long materielId,
            @RequestParam LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        Integer availableQuantity = affectationService.getAvailableQuantity(materielId);
        boolean isAvailable = availableQuantity > 0;

        Map<String, Object> response = new HashMap<>();
        response.put("materielId", materielId);
        response.put("isAvailable", isAvailable);
        response.put("availableQuantity", availableQuantity);
        response.put("startDate", startDate.toString());
        response.put("endDate", endDate != null ? endDate.toString() : null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<AffectationMaterielDTO>> createBatchAffectations(@RequestBody BatchAffectationRequestDTO batchRequest) {
        List<AffectationMaterielDTO> created = affectationService.createBatchAffectations(batchRequest);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/materiel/{materielId}")
    public ResponseEntity<List<AffectationMaterielDTO>> getAffectationsByMateriel(@PathVariable Long materielId) {
        return ResponseEntity.ok(affectationService.getAffectationsByMateriel(materielId));
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<AffectationMaterielDTO>> getAffectationsByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(affectationService.getAffectationsByMission(missionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AffectationMaterielDTO> getAffectationById(@PathVariable Long id) {
        return ResponseEntity.ok(affectationService.getAffectationById(id));
    }

    @GetMapping
    public ResponseEntity<List<AffectationMaterielDTO>> getAllAffectations() {
        return ResponseEntity.ok(affectationService.getAllAffectations());
    }

    @PostMapping
    public ResponseEntity<AffectationMaterielDTO> createAffectation(@RequestBody AffectationMaterielDTO dto) {
        AffectationMaterielDTO created = affectationService.createAffectation(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AffectationMaterielDTO> updateAffectation(@PathVariable Long id, @RequestBody AffectationMaterielDTO dto) {
        AffectationMaterielDTO updated = affectationService.updateAffectation(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAffectation(@PathVariable Long id) {
        affectationService.deleteAffectation(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/materiel/{materielId}")
    public ResponseEntity<Void> deleteAffectationsByMateriel(@PathVariable Long materielId) {
        affectationService.deleteAffectationsByMateriel(materielId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/mission/{missionId}")
    public ResponseEntity<Void> deleteAffectationsByMission(@PathVariable Long missionId) {
        affectationService.deleteAffectationsByMission(missionId);
        return ResponseEntity.noContent().build();
    }
}