package com.pfe.webapp.controller;

import com.pfe.webapp.dto.FichierDTO;
import com.pfe.webapp.dto.project.ProjectResponseDTO;
import com.pfe.webapp.dto.rapport.RapportRequestDTO;
import com.pfe.webapp.dto.rapport.RapportResponseDTO;
import com.pfe.webapp.dto.rendement.RendementResponseDTO;
import com.pfe.webapp.service.FichierService;
import com.pfe.webapp.service.FichierServiceImpl;
import com.pfe.webapp.service.RapportService;
import com.pfe.webapp.service.RendementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapports")
@CrossOrigin(origins = "http://localhost:4200")
public class RapportController {

    @Autowired
    private RapportService rapportService;

    @Autowired
    private RendementService rendementService;

    @Autowired
    private FichierService fichierService;
    // ========== EXISTING ENDPOINTS ==========

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<RapportResponseDTO>> getRapportsByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(rapportService.getRapportsByMission(missionId));
    }

    @GetMapping("/mission/{missionId}/current-project")
    public ResponseEntity<List<RapportResponseDTO>> getRapportsForCurrentProject(@PathVariable Long missionId) {
        return ResponseEntity.ok(rapportService.getRapportsForCurrentProject(missionId));
    }

    @PostMapping("/mission/{missionId}")
    public ResponseEntity<RapportResponseDTO> addRapportToCurrentProject(
            @PathVariable Long missionId,
            @RequestBody RapportRequestDTO request) {
        RapportResponseDTO created = rapportService.addRapportToCurrentProject(request, missionId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RapportResponseDTO> updateRapport(
            @PathVariable Long id,
            @RequestBody RapportRequestDTO request) {
        return ResponseEntity.ok(rapportService.updateRapport(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRapport(@PathVariable Long id) {
        rapportService.deleteRapport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RapportResponseDTO> getRapportById(@PathVariable Long id) {
        return ResponseEntity.ok(rapportService.getRapportById(id));
    }

    @GetMapping("/mission/{missionId}/search")
    public ResponseEntity<List<RapportResponseDTO>> searchRapports(
            @PathVariable Long missionId,
            @RequestParam String keyword) {
        return ResponseEntity.ok(rapportService.searchRapports(missionId, keyword));
    }

    @GetMapping("/{rapportId}/rendements/equipe/{equipeId}")
    public ResponseEntity<List<RendementResponseDTO>> getRendementsByRapportAndEquipe(
            @PathVariable Long rapportId,
            @PathVariable Long equipeId) {
        return ResponseEntity.ok(rendementService.getRendementsByRapportAndEquipe(rapportId, equipeId));
    }

    // ========== NEW ENDPOINTS FOR REPORTS VIEWER ==========

    /**
     * Get all projects for current user's mission (for sidebar)
     * GET /api/rapports/projects
     */
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByCurrentMission(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(rapportService.getProjectsByCurrentMission(userDetails));
    }

    /**
     * Get reports by project ID (for reports viewer)
     * GET /api/rapports/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<RapportResponseDTO>> getReportsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(rapportService.getReportsByProject(projectId));
    }

    /**
     * Search reports by keyword and optionally project ID
     * GET /api/rapports/search?keyword=test&projectId=1
     */
    @GetMapping("/search")
    public ResponseEntity<List<RapportResponseDTO>> searchReports(
            @RequestParam String keyword,
            @RequestParam(required = false) Long projectId) {
        return ResponseEntity.ok(rapportService.searchReports(keyword, projectId));
    }

    // Add this method to RapportController
    @GetMapping("/{id}/fichiers")
    public ResponseEntity<List<FichierDTO>> getFichiersByRapport(@PathVariable Long id) {
        return ResponseEntity.ok(fichierService.getFichiersByRapport(id));
    }
}