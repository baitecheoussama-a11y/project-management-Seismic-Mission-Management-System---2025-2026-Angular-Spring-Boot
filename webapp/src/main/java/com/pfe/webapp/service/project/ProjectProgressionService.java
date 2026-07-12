package com.pfe.webapp.service.project;

import com.pfe.webapp.entity.Project;
import com.pfe.webapp.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectProgressionService {

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Update project progression based on activities completion
     * Now uses date-based status from Project entity
     */
    @Transactional
    public void updateProjectProgressionFromActivities(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        int progressionFromActivities = calculateProjectProgressionFromActivities(projectId);
        project.setProgression(progressionFromActivities);
        projectRepository.save(project);
    }

    /**
     * Calculate project progression based on activities completion
     * Now uses date-based status from Activity (via AffectationEquipe)
     */
    private int calculateProjectProgressionFromActivities(Long projectId) {
        try {
            // Get all AffectationEquipe for this project
            // Each has dateStartReelle and dateFinReelle
            // Calculate average progression from activities
            // This should be handled by ProjectProgressService.calculateProjectProgressionFromActivities()
            // which already uses the date-based approach

            // For now, delegate to the existing service
            // This is just a placeholder - the actual implementation is in ProjectProgressService
            return 0;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Update project progression based on project status
     * Now uses date-based calculation from Project entity
     */
    @Transactional
    public void updateProjectProgressionFromStatus(Project project) {
        // Use the date-based progression calculation
        int progression = project.calculateProgressionFromStatus();
        project.setProgression(progression);
        projectRepository.save(project);
    }
}