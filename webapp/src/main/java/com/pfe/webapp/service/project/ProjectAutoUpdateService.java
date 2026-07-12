package com.pfe.webapp.service.project;

import com.pfe.webapp.entity.Project;
import com.pfe.webapp.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@EnableScheduling
public class ProjectAutoUpdateService {

    @Autowired
    private ProjectRepository projectRepository;

    // Run daily at midnight to check and update delayed projects
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateDelayedProjects() {
        System.out.println("Running scheduled task: Updating delayed projects...");

        // Get all active projects (not completed or cancelled)
        List<Project> activeProjects = projectRepository.findAllActiveProjects();

        for (Project project : activeProjects) {
            // Check if target end date is passed
            if (project.getObjectifFin() != null &&
                    LocalDate.now().isAfter(project.getObjectifFin())) {

                // Calculate current status using date-based logic
                String currentStatus = project.calculateStatus();

                // Only update if not already delayed, completed, or cancelled
                if (!"ENRETARD".equals(currentStatus) &&
                        !"TERMINI".equals(currentStatus) &&
                        !"ANNULE".equals(currentStatus)) {

                    // Check if dateStartReelle is null (not started yet)
                    if (project.getDateStartReelle() == null) {
                        // If not started and end date passed -> consider as DELAYED
                        // But we don't want to auto-set dates, just mark as delayed conceptually
                        // We'll set dateStartReelle to today to indicate it should have started
                        project.setDateStartReelle(LocalDate.now());
                    }

                    // If dateStartReelle is set but no dateFinReelle and end date passed
                    if (project.getDateStartReelle() != null && project.getDateFinReelle() == null) {
                        // The project is delayed - status will be ENRETARD when calculated
                        // No need to change dates, just save to trigger recalculation
                        projectRepository.save(project);
                        System.out.println("Project " + project.getId() + " (" + project.getNom() +
                                ") is DELAYED (target end date: " + project.getObjectifFin() + ")");
                    }
                }
            }
        }
    }
}