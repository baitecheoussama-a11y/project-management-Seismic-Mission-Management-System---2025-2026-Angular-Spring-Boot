package com.pfe.webapp.controller.materiel;

import com.pfe.webapp.dto.materiel.AffectationMaterielToActiveDTO;
import com.pfe.webapp.dto.materiel.AssignMaterielToActiveRequestDTO;
import com.pfe.webapp.dto.materiel.UpdateMaterielToActiveRequestDTO;
import com.pfe.webapp.service.materiel.AffectationMaterielToActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materiel-affectations-to-active")
@CrossOrigin(origins = "http://localhost:4200")
public class AffectationMaterielToActiveController {

    @Autowired
    private AffectationMaterielToActiveService affectationService;

    @PostMapping
    public ResponseEntity<AffectationMaterielToActiveDTO> assignMaterielToActive(
            @RequestBody AssignMaterielToActiveRequestDTO request) {
        AffectationMaterielToActiveDTO created = affectationService.assignMaterielToActive(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/materiel/{materielId}")
    public ResponseEntity<List<AffectationMaterielToActiveDTO>> getByMaterielId(@PathVariable Long materielId) {
        return ResponseEntity.ok(affectationService.getByMaterielId(materielId));
    }

    @GetMapping("/active/{activeId}")
    public ResponseEntity<List<AffectationMaterielToActiveDTO>> getByActiveId(@PathVariable Long activeId) {
        return ResponseEntity.ok(affectationService.getByActiveId(activeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AffectationMaterielToActiveDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(affectationService.getById(id));
    }

    @GetMapping("/materiel/{materielId}/active")
    public ResponseEntity<List<AffectationMaterielToActiveDTO>> getActiveByMaterielId(@PathVariable Long materielId) {
        return ResponseEntity.ok(affectationService.getActiveByMaterielId(materielId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AffectationMaterielToActiveDTO> update(
            @PathVariable Long id,
            @RequestBody UpdateMaterielToActiveRequestDTO request) {
        return ResponseEntity.ok(affectationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        affectationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/materiel/{materielId}/active/{activeId}")
    public ResponseEntity<Void> deleteByMaterielAndActive(
            @PathVariable Long materielId,
            @PathVariable Long activeId) {
        affectationService.deleteByMaterielAndActive(materielId, activeId);
        return ResponseEntity.noContent().build();
    }
}