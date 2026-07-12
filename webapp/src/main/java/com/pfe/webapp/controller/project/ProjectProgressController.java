// controller/project/ProjectProgressController.java
package com.pfe.webapp.controller.project;

import com.pfe.webapp.dto.project.ActivityProgressDTO;
import com.pfe.webapp.dto.project.ProjectProgressStatsDTO;
import com.pfe.webapp.service.project.ProjectProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project-progress")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectProgressController {

    @Autowired
    private ProjectProgressService projectProgressService;

    @GetMapping("/stats/{projectId}")
    public ResponseEntity<ProjectProgressStatsDTO> getProjectProgressStats(@PathVariable Long projectId) {
        ProjectProgressStatsDTO stats = projectProgressService.getProjectProgressStats(projectId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/activities/{projectId}")
    public ResponseEntity<List<ActivityProgressDTO>> getActivitiesByProject(@PathVariable Long projectId) {
        List<ActivityProgressDTO> activities = projectProgressService.getActivitiesByProject(projectId);
        return ResponseEntity.ok(activities);
    }
}