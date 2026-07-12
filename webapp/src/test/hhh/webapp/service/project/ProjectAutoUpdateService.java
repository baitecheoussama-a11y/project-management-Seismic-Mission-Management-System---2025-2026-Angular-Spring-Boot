package com.pfe.webapp.service.project;

import com.pfe.webapp.entity.Project;
import com.pfe.webapp.repository.ProjectRepository;
import com.pfe.webapp.service.av.EtatAvancementService;
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

    @Autowired
    private EtatAvancementService etatAvancementService;

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

                // Get current status
                if (project.getEtatAvancements() != null && !project.getEtatAvancements().isEmpty()) {
                    var projectStatus = project.getEtatAvancements().stream()
                            .filter(e -> e.getActive() == null)
                            .findFirst();

                    if (projectStatus.isPresent()) {
                        var currentStatus = projectStatus.get().getStatus();
                        // Only update if not already delayed, completed, or cancelled
                        if (currentStatus != com.pfe.webapp.entity.StatusEtatAvancement.ENRETARD &&
                                currentStatus != com.pfe.webapp.entity.StatusEtatAvancement.TERMINI &&
                                currentStatus != com.pfe.webapp.entity.StatusEtatAvancement.ANNULE) {

                            // Update to ENRETARD
                            etatAvancementService.updateEtatAvancementStatus(
                                    projectStatus.get().getId(),
                                    "ENRETARD"
                            );
                            System.out.println("Project " + project.getId() + " (" + project.getNom() +
                                    ") marked as DELAYED (target end date: " + project.getObjectifFin() + ")");
                        }
                    }
                }
            }
        }
    }
}