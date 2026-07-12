// controller/RapportDetailsController.java
package com.pfe.webapp.controller;

import com.pfe.webapp.dto.rapport.RapportDetailsRequestDTO;
import com.pfe.webapp.entity.Rapport;
import com.pfe.webapp.mongodb.document.RapportDetailsDocument;
import com.pfe.webapp.mongodb.service.RapportDetailsService;
import com.pfe.webapp.service.RapportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rapport-details")
@CrossOrigin(origins = "http://localhost:4200")
public class RapportDetailsController {

    @Autowired
    private RapportDetailsService rapportDetailsService;

    @Autowired
    private RapportService rapportService;

    // controller/RapportDetailsController.java
    @PostMapping
    public ResponseEntity<?> saveRapportDetails(@RequestBody RapportDetailsRequestDTO request) {
        try {
            // ✅ استخدم getRapportEntityById للحصول على Entity
            Rapport rapport = rapportService.getRapportEntityById(request.getRapportId());
            if (rapport == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Rapport not found with id: " + request.getRapportId()));
            }

            // Check if details already exist
            if (rapportDetailsService.existsByRapportId(request.getRapportId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Details already exist for this rapport. Use PUT to update."));
            }

            // Save to MongoDB
            RapportDetailsDocument saved = rapportDetailsService.save(
                    request.getRapportId(),
                    rapport.getProject().getId(),
                    request.getDetails()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/{rapportId}")
    public ResponseEntity<?> updateRapportDetails(
            @PathVariable Long rapportId,
            @RequestBody Map<String, Object> details) {
        try {
            RapportDetailsDocument doc = rapportDetailsService.getByRapportId(rapportId)
                    .orElseThrow(() -> new RuntimeException("Details not found"));

            doc.setDetails(details);
            RapportDetailsDocument updated = rapportDetailsService.save(doc);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{rapportId}")
    public ResponseEntity<RapportDetailsDocument> getByRapportId(@PathVariable Long rapportId) {
        return rapportDetailsService.getByRapportId(rapportId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<RapportDetailsDocument>> getByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(rapportDetailsService.getByProjectId(projectId));
    }

    @GetMapping
    public ResponseEntity<List<RapportDetailsDocument>> getAll() {
        return ResponseEntity.ok(rapportDetailsService.getAll());
    }

    @DeleteMapping("/{rapportId}")
    public ResponseEntity<Void> deleteByRapportId(@PathVariable Long rapportId) {
        rapportDetailsService.deleteByRapportId(rapportId);
        return ResponseEntity.noContent().build();
    }
}