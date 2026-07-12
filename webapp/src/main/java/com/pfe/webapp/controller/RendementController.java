package com.pfe.webapp.controller;

import com.pfe.webapp.dto.rendement.RendementRequestDTO;
import com.pfe.webapp.dto.rendement.RendementResponseDTO;
import com.pfe.webapp.service.RendementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rendements")
@CrossOrigin(origins = "http://localhost:4200")
public class RendementController {

    @Autowired
    private RendementService rendementService;

    @GetMapping("/rapport/{rapportId}")
    public ResponseEntity<List<RendementResponseDTO>> getRendementsByRapport(@PathVariable Long rapportId) {
        return ResponseEntity.ok(rendementService.getRendementsByRapport(rapportId));
    }

    // ✅ FIXED: Now receives activeId from request body, not from path
    @PostMapping("/rapport/{rapportId}/equipe/{equipeId}")
    public ResponseEntity<RendementResponseDTO> addRendementToRapport(
            @PathVariable Long rapportId,
            @PathVariable Long equipeId,
            @RequestBody RendementRequestDTO request) {
        // Mission ID will be derived from the rapport
        RendementResponseDTO created = rendementService.addRendementToRapport(rapportId, equipeId, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RendementResponseDTO> updateRendement(
            @PathVariable Long id,
            @RequestBody RendementRequestDTO request) {
        return ResponseEntity.ok(rendementService.updateRendement(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRendement(@PathVariable Long id) {
        rendementService.deleteRendement(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rapport/{rapportId}/equipe/{equipeId}")
    public ResponseEntity<List<RendementResponseDTO>> getRendementsByRapportAndEquipe(
            @PathVariable Long rapportId,
            @PathVariable Long equipeId) {
        return ResponseEntity.ok(rendementService.getRendementsByRapportAndEquipe(rapportId, equipeId));
    }

    @GetMapping("/equipe/{equipeId}/mission/{missionId}")
    public ResponseEntity<List<RendementResponseDTO>> getRendementsByEquipeAndMission(
            @PathVariable Long equipeId,
            @PathVariable Long missionId) {
        return ResponseEntity.ok(rendementService.getRendementsByEquipeAndMission(equipeId, missionId));
    }
}