// Create new file: ProjectProgressionService.java
package com.pfe.webapp.service.project;

import com.pfe.webapp.entity.EtatAvancement;
import com.pfe.webapp.entity.Project;
import com.pfe.webapp.entity.StatusEtatAvancement;
import com.pfe.webapp.repository.av.EtatAvancementRepository;
import com.pfe.webapp.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectProgressionService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EtatAvancementRepository etatAvancementRepository;

    /**
     * Update project progression based on activities completion
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
     */
    private int calculateProjectProgressionFromActivities(Long projectId) {
        try {
            List<EtatAvancement> activityStatuses = etatAvancementRepository.findByProjectIdAndActiveIsNotNull(projectId);

            if (activityStatuses == null || activityStatuses.isEmpty()) {
                return 0;
            }

            double totalCompletion = 0;

            for (EtatAvancement etat : activityStatuses) {
                int completionPercentage = 0;

                if (etat.getStatus() != null) {
                    switch (etat.getStatus()) {
                        case TERMINI:
                            completionPercentage = 100;
                            break;
                        case ENCOURS:
                            completionPercentage = 50;
                            break;
                        case ENATTENTE:
                            completionPercentage = 25;
                            break;
                        case ENRETARD:
                            completionPercentage = 40;
                            break;
                        default:
                            completionPercentage = 0;
                    }
                }
                totalCompletion += completionPercentage;
            }

            return (int) Math.round(totalCompletion / activityStatuses.size());

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Update project progression based on project status
     */
    @Transactional
    public void updateProjectProgressionFromStatus(Project project) {
        project.updateProgressionFromStatus();
        projectRepository.save(project);
    }
}