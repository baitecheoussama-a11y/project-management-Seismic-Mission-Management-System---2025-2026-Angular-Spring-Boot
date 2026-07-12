package com.pfe.webapp.config;

import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.*;
import com.pfe.webapp.repository.av.AvancementRepository;
import com.pfe.webapp.repository.av.EtatAvancementRepository;
import com.pfe.webapp.repository.rapport.RapportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Order(3) // Run after MissionInitializer
public class ProjectInitializer implements CommandLineRunner {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private CoordonneeRepository coordonneeRepository;

    @Autowired
    private WilayaRepository wilayaRepository;

    @Autowired
    private RapportRepository rapportRepository;

    @Autowired
    private EtatAvancementRepository etatAvancementRepository;

    @Autowired
    private AvancementRepository avancementRepository;

    private final Random random = new Random();

    // Sample project names
    private final String[] projectNames = {
            "Alpha", "Beta", "Gamma", "Delta", "Epsilon",
            "Zeta", "Eta", "Theta", "Iota", "Kappa",
            "Lambda", "Mu", "Nu", "Xi", "Omicron",
            "Pi", "Rho", "Sigma", "Tau", "Upsilon",
            "Phi", "Chi", "Psi", "Omega"
    };

    private final String[] projectDescriptions = {
            "Modernisation de l'infrastructure IT",
            "Migration vers le cloud",
            "Développement d'application mobile",
            "Implémentation ERP",
            "Sécurisation des données",
            "Optimisation des processus",
            "Digitalisation des services",
            "Intégration IA",
            "Refonte du système legacy",
            "Déploiement de solutions IoT",
            "Analyse et Big Data",
            "Automatisation des tâches"
    };

    private final String[] reportTitles = {
            "Rapport d'avancement mensuel",
            "Rapport technique détaillé",
            "Bilan de mi-parcours",
            "Rapport de performance",
            "Évaluation des risques",
            "Rapport de conformité",
            "Analyse des coûts",
            "Rapport de sécurité",
            "Bilan des réalisations",
            "Rapport de qualité"
    };

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("\n🚀 Starting ProjectInitializer...");

        // ✅ Check if projects already exist
        if (projectRepository.count() > 0) {
            System.out.println("ℹ️ Projects already exist in the database. Skipping creation.");
            long totalProjects = projectRepository.count();
            long totalSites = siteRepository.count();
            long totalReports = rapportRepository.count();
            System.out.println("   - Existing projects: " + totalProjects);
            System.out.println("   - Existing sites: " + totalSites);
            System.out.println("   - Existing reports: " + totalReports);
            System.out.println("\n✅ PROJECTS INITIALIZATION SKIPPED (already exists)");
            return;
        }

        List<Mission> allMissions = missionRepository.findAll();

        if (allMissions.isEmpty()) {
            System.out.println("⚠️ No missions found. Please run MissionInitializer first.");
            return;
        }

        int totalProjects = 0;
        int totalReports = 0;
        int totalSites = 0;

        for (int i = 0; i < allMissions.size(); i++) {
            Mission mission = allMissions.get(i);
            int projectCount = createProjectsForMission(mission, i);
            totalProjects += projectCount;
            System.out.println("   ✅ Created " + projectCount + " projects for mission: " + mission.getCodeMission());
        }

        // Count reports and sites
        for (Mission mission : allMissions) {
            if (mission.getProjects() != null) {
                for (Project project : mission.getProjects()) {
                    if (project.getRapports() != null) {
                        totalReports += project.getRapports().size();
                    }
                    if (project.getSite() != null) {
                        totalSites++;
                    }
                }
            }
        }

        System.out.println("\n🎉 ==========================================");
        System.out.println("✅ PROJECTS INITIALIZATION COMPLETED SUCCESSFULLY!");
        System.out.println("   - " + totalProjects + " Projects created");
        System.out.println("   - " + totalReports + " Reports created");
        System.out.println("   - " + totalSites + " Sites created with coordinates");
        System.out.println("==========================================\n");
    }

    private int createProjectsForMission(Mission mission, int missionIndex) {
        int totalProjects = 0;
        int activeProjectCount = 0;

        // First 5 missions: 5-10 old projects + 1 active
        if (missionIndex < 5) {
            // Create old projects (completed or cancelled)
            int oldProjectsCount = ThreadLocalRandom.current().nextInt(5, 11);
            for (int i = 0; i < oldProjectsCount; i++) {
                Project project = createProject(mission, false);
                createSiteAndCoordinates(project);
                createReports(project, ThreadLocalRandom.current().nextInt(2, 6));
                totalProjects++;
            }

            // Create 1 active project
            Project activeProject = createProject(mission, true);
            createSiteAndCoordinates(activeProject);
            createReports(activeProject, ThreadLocalRandom.current().nextInt(1, 4));
            totalProjects++;
            activeProjectCount++;

        } else {
            // Other missions: 3-7 old projects + 1 active
            int oldProjectsCount = ThreadLocalRandom.current().nextInt(3, 8);
            for (int i = 0; i < oldProjectsCount; i++) {
                Project project = createProject(mission, false);
                createSiteAndCoordinates(project);
                createReports(project, ThreadLocalRandom.current().nextInt(2, 5));
                totalProjects++;
            }

            // Create 1 active project
            Project activeProject = createProject(mission, true);
            createSiteAndCoordinates(activeProject);
            createReports(activeProject, ThreadLocalRandom.current().nextInt(1, 3));
            totalProjects++;
            activeProjectCount++;
        }

        System.out.println("      - " + totalProjects + " total projects (" + activeProjectCount + " active)");
        return totalProjects;
    }

    private Project createProject(Mission mission, boolean isActive) {
        String projectName = getRandomProjectName(mission.getId());
        String description = projectDescriptions[random.nextInt(projectDescriptions.length)];

        // Random dates
        LocalDate startDate;
        LocalDate endDate;
        LocalDate dateFinReelle = null;

        if (isActive) {
            // Active project: starts in the past, ends in the future
            startDate = LocalDate.now().minusMonths(random.nextInt(6) + 1);
            endDate = LocalDate.now().plusMonths(random.nextInt(12) + 3);
        } else {
            // Old project: completed/cancelled in the past
            startDate = LocalDate.now().minusMonths(random.nextInt(24) + 12);
            endDate = startDate.plusMonths(random.nextInt(12) + 6);
            dateFinReelle = endDate.plusDays(random.nextInt(30));
        }

        Project project = new Project();
        project.setNom(projectName);
        project.setDescription(description);
        project.setBudget(ThreadLocalRandom.current().nextDouble(50000, 500000));
        project.setObjectifVP(ThreadLocalRandom.current().nextInt(10000, 100000));
        project.setObjectifDebut(startDate);
        project.setObjectifFin(endDate);
        project.setAnnule(false);
        project.setMission(mission);

        // Set progression based on status
        if (isActive) {
            project.setProgression(ThreadLocalRandom.current().nextInt(20, 80));
        } else {
            project.setProgression(100);
        }

        Project savedProject = projectRepository.save(project);

        // Create EtatAvancement for the project
        EtatAvancement etat = createEtatAvancement(savedProject, isActive, dateFinReelle);
        savedProject.addEtatAvancement(etat);

        // Update progression from status
        savedProject.updateProgressionFromStatus();

        if (dateFinReelle != null && !isActive) {
            savedProject.setDateFinReelle(dateFinReelle);
        }

        return projectRepository.save(savedProject);
    }

    private EtatAvancement createEtatAvancement(Project project, boolean isActive, LocalDate dateFinReelle) {
        EtatAvancement etat = new EtatAvancement();
        etat.setProject(project);
        etat.setActive(null);
        etat.setDateLastAvancement(LocalDate.now());

        StatusEtatAvancement status;

        if (isActive) {
            // Active project: random status (PLANIFIER, ENCOURS, ENATTENTE, ENRETARD)
            StatusEtatAvancement[] activeStatuses = {
                    StatusEtatAvancement.PLANIFIER,
                    StatusEtatAvancement.ENCOURS,
                    StatusEtatAvancement.ENATTENTE,
                    StatusEtatAvancement.ENRETARD
            };
            status = activeStatuses[random.nextInt(activeStatuses.length)];
        } else {
            // Old project: completed or cancelled
            status = random.nextBoolean() ? StatusEtatAvancement.TERMINI : StatusEtatAvancement.ANNULE;
            if (status == StatusEtatAvancement.TERMINI) {
                project.setAnnule(false);
            } else {
                project.setAnnule(true);
            }
        }

        etat.setStatus(status);

        // Add some avancements
        int avancementCount = ThreadLocalRandom.current().nextInt(1, 5);
        for (int i = 0; i < avancementCount; i++) {
            Avancement avancement = new Avancement();
            avancement.setTitre("Avancement " + (i + 1));
            avancement.setDate(LocalDate.now().minusDays(random.nextInt(30)));
            avancement.setResume("Progression du projet phase " + (i + 1));
            avancement.setEtatAvancement(etat);
            etat.addAvancement(avancement);
        }

        return etatAvancementRepository.save(etat);
    }

    private void createSiteAndCoordinates(Project project) {
        // Get random wilaya
        List<Wilaya> wilayas = wilayaRepository.findAll();
        if (wilayas.isEmpty()) {
            System.out.println("⚠️ No wilayas found. Please run WilayaInitializer first.");
            return;
        }

        Wilaya wilaya = wilayas.get(random.nextInt(wilayas.size()));

        Site site = new Site();
        site.setProject(project);
        site.setWilaya(wilaya);
        site.setSurface(ThreadLocalRandom.current().nextDouble(10, 500));

        Site savedSite = siteRepository.save(site);

        // Create 3-6 coordinates for the site
        int coordCount = ThreadLocalRandom.current().nextInt(4, 8);
        List<Coordonnee> coordinates = new ArrayList<>();

        for (int i = 0; i < coordCount; i++) {
            Coordonnee coord = new Coordonnee();
            // Random coordinates around wilaya center
            double latOffset = (random.nextDouble() - 0.5) * 2;
            double lngOffset = (random.nextDouble() - 0.5) * 2;

            coord.setLatitude(wilaya.getCenterLatitude() + latOffset);
            coord.setLongitude(wilaya.getCenterLongitude() + lngOffset);
            coord.setOrdre(i + 1);
            coord.setSite(savedSite);
            coordinates.add(coord);
        }

        coordonneeRepository.saveAll(coordinates);
        savedSite.setCoordonnees(coordinates);
        siteRepository.save(savedSite);
    }

    private void createReports(Project project, int count) {
        for (int i = 0; i < count; i++) {
            Rapport rapport = new Rapport();
            rapport.setTitre(reportTitles[random.nextInt(reportTitles.length)] + " - " + project.getNom());
            rapport.setDate(LocalDate.now().minusDays(random.nextInt(60)));
            rapport.setResume("Ce rapport détaille l'avancement du projet " + project.getNom() +
                    ". Les objectifs principaux ont été atteints avec un taux de satisfaction de " +
                    ThreadLocalRandom.current().nextInt(60, 95) + "%.");
            rapport.setProject(project);
            rapportRepository.save(rapport);
        }
    }

    private String getRandomProjectName(Long missionId) {
        String name = projectNames[random.nextInt(projectNames.length)];
        String suffix = String.valueOf(ThreadLocalRandom.current().nextInt(1, 99));
        return name + "-" + suffix;
    }
}