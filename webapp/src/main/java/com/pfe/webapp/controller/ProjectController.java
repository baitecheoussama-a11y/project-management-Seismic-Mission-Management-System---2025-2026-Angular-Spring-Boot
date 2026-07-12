// controller/ProjectController.java
package com.pfe.webapp.controller;

import com.pfe.webapp.dto.project.*;
import com.pfe.webapp.entity.Compte;
import com.pfe.webapp.entity.Employe;
import com.pfe.webapp.repository.CompteRepository;
import com.pfe.webapp.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CompteRepository compteRepository;

    // ========== STATIC PATHS (BEFORE dynamic paths) ==========

    // Get all projects for current mission (for reports viewer)
        @GetMapping("/current-mission")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<List<ProjectResponseDTO>> getProjectsByCurrentMission(
                @AuthenticationPrincipal UserDetails userDetails) {
            try {
                Compte compte = compteRepository.findByUsername(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));

                Employe employe = compte.getEmploye();
                if (employe == null) {
                    return ResponseEntity.badRequest().build();
                }

                List<ProjectResponseDTO> projects = projectService.getProjectsByCurrentMission(employe.getId());
                return ResponseEntity.ok(projects);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

    // Get active project for current user's mission
    @GetMapping("/my-project")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyActiveProject(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Compte compte = compteRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Employe employe = compte.getEmploye();
            if (employe == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "No employee associated with this account"));
            }

            ProjectResponseDTO project = projectService.getProjectForEmployee(employe.getId());

            if (project == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No active project found for your current mission"));
            }

            return ResponseEntity.ok(Map.of(
                    "hasProject", true,
                    "projectId", project.getId(),
                    "projectName", project.getNom()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get active project by mission ID
    @GetMapping("/mission/{missionId}/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getActiveProjectByMission(@PathVariable Long missionId) {
        ProjectResponseDTO project = projectService.getActiveProjectByMissionId(missionId);
        if (project == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(project);
    }

    // Get current project by mission ID
    @GetMapping("/mission/{missionId}/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectResponseDTO> getCurrentProjectByMission(@PathVariable Long missionId) {
        ProjectResponseDTO project = projectService.getCurrentProjectByMission(missionId);
        if (project == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(project);
    }

    // ========== DYNAMIC PATHS (PUT AFTER static paths) ==========

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_MISSION')")
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody ProjectRequestDTO request) {
        ProjectResponseDTO created = projectService.createProject(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_MISSION')")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long id, @RequestBody ProjectRequestDTO request) {
        ProjectResponseDTO updated = projectService.updateProject(id, request);
        return ResponseEntity.ok(updated);
    }

    // Add rapport to project
    @PostMapping("/{projectId}/rapports")
    @PreAuthorize("hasAnyRole('ADMIN', 'Gestionnaire', 'CHEF_TERRAIN')")
    public ResponseEntity<RapportResponseDTO> addRapport(
            @PathVariable Long projectId,
            @RequestBody RapportRequestDTO request) {
        RapportResponseDTO rapport = projectService.addRapport(projectId, request);
        return new ResponseEntity<>(rapport, HttpStatus.CREATED);
    }

    // Get all rapports by project
    @GetMapping("/{projectId}/rapports")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RapportResponseDTO>> getRapportsByProjectId(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getRapportsByProjectId(projectId));
    }

    // Get rapport by ID
    @GetMapping("/rapports/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RapportResponseDTO> getRapportById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getRapportById(id));
    }

    // Update rapport
    @PutMapping("/rapports/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'Gestionnaire', 'CHEF_TERRAIN')")
    public ResponseEntity<RapportResponseDTO> updateRapport(
            @PathVariable Long id,
            @RequestBody RapportRequestDTO request) {
        return ResponseEntity.ok(projectService.updateRapport(id, request));
    }

    // Delete rapport
    @DeleteMapping("/rapports/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'Gestionnaire')")
    public ResponseEntity<Void> deleteRapport(@PathVariable Long id) {
        projectService.deleteRapport(id);
        return ResponseEntity.noContent().build();
    }

    // أضف هذه endpoints في ProjectController.java الموجود

    @GetMapping("/{projectId}/progress-stats")
    public ResponseEntity<ProjectProgressStatsDTO> getProjectProgressStats(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectProgressStats(projectId));
    }

    // In ProjectController.java - Add this endpoint

    // ProjectController.java

    @PutMapping("/{projectId}/real-dates")
    public ResponseEntity<ProjectResponseDTO> updateProjectRealDates(
            @PathVariable Long projectId,
            @RequestBody Map<String, String> request) {
        LocalDate dateStartReelle = request.get("dateStartReelle") != null ?
                LocalDate.parse(request.get("dateStartReelle")) : null;
        LocalDate dateFinReelle = request.get("dateFinReelle") != null ?
                LocalDate.parse(request.get("dateFinReelle")) : null;

        ProjectResponseDTO updated = projectService.updateProjectRealDates(projectId, dateStartReelle, dateFinReelle);
        return ResponseEntity.ok(updated);
    }


    // In ProjectController.java

    @PutMapping("/{projectId}/cancel")
    public ResponseEntity<ProjectResponseDTO> cancelProject(@PathVariable Long projectId) {
        ProjectResponseDTO cancelled = projectService.cancelProject(projectId);
        return ResponseEntity.ok(cancelled);
    }

    @PostMapping("/{projectId}/update-progression")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_MISSION')")
    public ResponseEntity<?> updateProjectProgression(@PathVariable Long projectId) {
        try {
            projectService.updateProjectProgressionFromActivities(projectId);
            return ResponseEntity.ok(Map.of("message", "Project progression updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{projectId}/activities-progress")
    public ResponseEntity<List<ActivityProgressDTO>> getProjectActivitiesProgress(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectActivitiesProgress(projectId));
    }
    // In ProjectController.java - add this endpoint
    @GetMapping("/{id}/with-mission")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjectWithMissionDTO> getProjectWithMission(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectWithMission(id));
    }

    // أضف هذه الـ endpoints في ProjectController.java

    // ========== GET ALL PROJECTS (FOR ADMIN/DIRECTEUR) ==========
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        List<ProjectResponseDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    // ========== GET PROJECTS BY MISSION ID (FOR ADMIN/DIRECTEUR) ==========
    @GetMapping("/mission/{missionId}/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTEUR')")
    public ResponseEntity<List<ProjectResponseDTO>> getProjectsByMissionId(@PathVariable Long missionId) {
        List<ProjectResponseDTO> projects = projectService.getProjectsByMissionId(missionId);
        return ResponseEntity.ok(projects);
    }

    // ========== GET PROJECTS FOR CURRENT USER (BASED ON ROLE) ==========
    @GetMapping("/accessible")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjectResponseDTO>> getAccessibleProjects(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Compte compte = compteRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Employe employe = compte.getEmploye();
            if (employe == null) {
                return ResponseEntity.badRequest().build();
            }

            List<ProjectResponseDTO> projects = projectService.getAccessibleProjects(employe.getId());
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}