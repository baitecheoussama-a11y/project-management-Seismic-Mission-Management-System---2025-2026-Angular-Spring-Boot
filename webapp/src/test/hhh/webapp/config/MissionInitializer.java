package com.pfe.webapp.config;

import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.*;
import com.pfe.webapp.repository.event.EvenementRepository;
import com.pfe.webapp.repository.event.TypeEvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Order(2) // Run after DataInitializer
public class MissionInitializer implements CommandLineRunner {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private AffectationEmployeRepository affectationEmployeRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private TypeEvenementRepository typeEvenementRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    private final Random random = new Random();
    private final String[] eventTitles = {
            "Réunion de coordination", "Revue de sprint", "Démo client", "Atelier technique",
            "Formation sécurité", "Migration serveur", "Test utilisateur", "Validation recette",
            "Lancement phase 1", "Lancement phase 2", "Lancement phase 3", "Revue architecture",
            "Audit sécurité", "Optimisation base données", "Mise à jour documentation",
            "Formation équipe", "Réunion fournisseur", "Présentation comité",
            "Analyse post-mortem", "Plan de reprise", "Exercice sécurité", "Campagne test",
            "Déploiement préproduction", "Déploiement production", "Revue code",
            "Intégration continue", "Backup données", "Restoration test", "Pénétration test",
            "Réunion budget", "Revue planning", "Gestion risques", "Qualité logiciel"
    };

    private final String[] eventDescriptions = {
            "Coordination des équipes techniques et fonctionnelles",
            "Revue des réalisations du sprint et planification du prochain",
            "Présentation des fonctionnalités développées au client",
            "Session technique approfondie sur l'architecture",
            "Formation à la sécurité des données et bonnes pratiques",
            "Migration des serveurs vers nouvelle infrastructure",
            "Tests utilisateurs avec panel de 20 utilisateurs",
            "Validation de la recette fonctionnelle",
            "Démarrage officiel de la phase 1 du projet",
            "Démarrage de la phase 2 avec nouvelles fonctionnalités",
            "Démarrage de la phase finale du projet",
            "Revue complète de l'architecture technique",
            "Audit de sécurité par cabinet externe",
            "Optimisation des performances base de données",
            "Mise à jour complète documentation technique",
            "Formation des nouveaux membres de l'équipe",
            "Réunion avec fournisseur de solutions",
            "Présentation avancement au comité de direction",
            "Analyse des incidents et actions correctives",
            "Test du plan de reprise d'activité",
            "Exercice de simulation d'attaque informatique",
            "Campagne de tests intensifs",
            "Déploiement en environnement de préproduction",
            "Mise en production officielle",
            "Revue de code collégiale",
            "Mise en place intégration continue",
            "Sauvegarde complète des données",
            "Test de restauration des backups",
            "Test d'intrusion par équipe dédiée",
            "Revue budget projet",
            "Revue planning et jalons",
            "Identification et analyse des risques",
            "Contrôle qualité logiciel"
    };

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("\n🚀 Starting MissionInitializer...");

        // Create event types if they don't exist
        createEventTypes();

        // Create 11 missions if they don't exist
        createMissions();

        // Get all missions after creation
        List<Mission> allMissions = missionRepository.findAll();

        // Create events for missions (30-50 events per mission)
        createEventsForMissions(allMissions);

        // Assign users to missions
        assignUsersToMissions(allMissions);

        // Display summary
        displayMissionSummary(allMissions);

        System.out.println("\n🎉 ==========================================");
        System.out.println("✅ MISSIONS AND ASSIGNMENTS COMPLETED SUCCESSFULLY!");
        System.out.println("   - 11 Missions created");
        System.out.println("   - 6 Event Types created");

        // Calculate total events
        long totalEvents = evenementRepository.count();
        double avgEventsPerMission = (double) totalEvents / allMissions.size();
        System.out.println("   - " + totalEvents + " Events created (~" + String.format("%.0f", avgEventsPerMission) + " per mission)");
        System.out.println("   - 1 CHEF_MISSION per mission (11 total)");
        System.out.println("   - 1 CHEF_TERRAIN per mission (11 total)");
        System.out.println("   - 1 GESTIONNAIRE per mission (11 total)");
        System.out.println("==========================================\n");
    }

    private void createEventTypes() {
        if (typeEvenementRepository.count() >= 6) {
            System.out.println("ℹ️ Event types already exist, skipping creation");
            return;
        }

        List<TypeEvenement> eventTypes = Arrays.asList(
                new TypeEvenement("RÉUNION", "Réunion d'équipe ou de coordination", NiveauPriorite.MOYENNE),
                new TypeEvenement("LIVRABLE", "Date de livraison d'un livrable important", NiveauPriorite.ELEVEE),
                new TypeEvenement("JALON", "Étape importante du projet", NiveauPriorite.ELEVEE),
                new TypeEvenement("FORMATION", "Session de formation", NiveauPriorite.MOYENNE),
                new TypeEvenement("MAINTENANCE", "Période de maintenance système", NiveauPriorite.MOYENNE),
                new TypeEvenement("REVUE", "Revue de projet ou réunion de bilan", NiveauPriorite.FAIBLE)
        );

        for (TypeEvenement eventType : eventTypes) {
            Optional<TypeEvenement> existing = typeEvenementRepository.findByNom(eventType.getNom());
            if (existing.isEmpty()) {
                typeEvenementRepository.save(eventType);
                System.out.println("   ✅ Created Event Type: " + eventType.getNom());
            } else {
                System.out.println("   ⚠️ Event Type " + eventType.getNom() + " already exists");
            }
        }
    }

    private void createMissions() {
        // Check if missions already exist
        if (missionRepository.count() >= 11) {
            System.out.println("ℹ️ 11 missions already exist, skipping creation");
            return;
        }

        List<Mission> missions = new ArrayList<>();

        // Mission 1
        Mission m1 = new Mission();
        m1.setCodeMission("D2-001");
        m1.setMethodologie(TypeMission.D2);
        m1.setDescription("Mission D2 - Développement de l'application mobile clientèle");
        missions.add(m1);

        // Mission 2
        Mission m2 = new Mission();
        m2.setCodeMission("D3-001");
        m2.setMethodologie(TypeMission.D3);
        m2.setDescription("Mission D3 - Migration de base de données legacy vers cloud");
        missions.add(m2);

        // Mission 3
        Mission m3 = new Mission();
        m3.setCodeMission("D2-002");
        m3.setMethodologie(TypeMission.D2);
        m3.setDescription("Mission D2 - Développement plateforme e-commerce interne");
        missions.add(m3);

        // Mission 4
        Mission m4 = new Mission();
        m4.setCodeMission("D3-002");
        m4.setMethodologie(TypeMission.D3);
        m4.setDescription("Mission D3 - Support et maintenance systèmes existants");
        missions.add(m4);

        // Mission 5
        Mission m5 = new Mission();
        m5.setCodeMission("D2-003");
        m5.setMethodologie(TypeMission.D2);
        m5.setDescription("Mission D2 - Modernisation infrastructure réseau");
        missions.add(m5);

        // Mission 6
        Mission m6 = new Mission();
        m6.setCodeMission("D3-003");
        m6.setMethodologie(TypeMission.D3);
        m6.setDescription("Mission D3 - Création application RH avec intelligence artificielle");
        missions.add(m6);

        // Mission 7
        Mission m7 = new Mission();
        m7.setCodeMission("D2-004");
        m7.setMethodologie(TypeMission.D2);
        m7.setDescription("Mission D2 - Implémentation solution CRM intégrée");
        missions.add(m7);

        // Mission 8
        Mission m8 = new Mission();
        m8.setCodeMission("D3-004");
        m8.setMethodologie(TypeMission.D3);
        m8.setDescription("Mission D3 - Optimisation processus logistiques");
        missions.add(m8);

        // Mission 9
        Mission m9 = new Mission();
        m9.setCodeMission("D2-005");
        m9.setMethodologie(TypeMission.D2);
        m9.setDescription("Mission D2 - Développement application mobile terrain");
        missions.add(m9);

        // Mission 10
        Mission m10 = new Mission();
        m10.setCodeMission("D3-005");
        m10.setMethodologie(TypeMission.D3);
        m10.setDescription("Mission D3 - Sécurisation des données et conformité RGPD");
        missions.add(m10);

        // Mission 11
        Mission m11 = new Mission();
        m11.setCodeMission("D2-006");
        m11.setMethodologie(TypeMission.D2);
        m11.setDescription("Mission D2 - Refonte portail web institutionnel");
        missions.add(m11);

        int createdCount = 0;

        for (Mission mission : missions) {
            Mission existingMission = missionRepository.findByCodeMission(mission.getCodeMission());
            if (existingMission != null) {
                System.out.println("⚠️ Mission " + mission.getCodeMission() + " already exists, skipping...");
                continue;
            }

            missionRepository.save(mission);
            createdCount++;

            System.out.println("   ✅ Created Mission: " + mission.getCodeMission() + " | " +
                    mission.getMethodologie() + " | " +
                    truncateDescription(mission.getDescription(), 60));
        }

        System.out.println("\n✅ Created " + createdCount + " new missions");
    }

    private void createEventsForMissions(List<Mission> allMissions) {
        if (allMissions.isEmpty()) {
            System.out.println("⚠️ No missions found to create events for!");
            return;
        }

        // Get event types
        List<TypeEvenement> eventTypes = typeEvenementRepository.findAll();

        if (eventTypes.isEmpty()) {
            System.out.println("⚠️ Event types not found, skipping event creation");
            return;
        }

        System.out.println("\n📅 Creating events for missions (30-50 events per mission)...");

        int totalEventCount = 0;
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(6); // Start 6 months ago
        LocalDate endDate = today.plusMonths(12);   // End 12 months from now

        for (Mission mission : allMissions) {
            // Check if events already exist for this mission
            List<Evenement> existingEvents = evenementRepository.findByMissionId(mission.getId());
            if (!existingEvents.isEmpty() && existingEvents.size() >= 30) {
                System.out.println("   ⚠️ Events already exist for mission " + mission.getCodeMission() +
                        " (" + existingEvents.size() + " events), skipping...");
                totalEventCount += existingEvents.size();
                continue;
            }

            // Delete existing events for this mission if we want to regenerate
            if (!existingEvents.isEmpty()) {
                evenementRepository.deleteAll(existingEvents);
                System.out.println("   🗑️ Deleted " + existingEvents.size() + " existing events for " + mission.getCodeMission());
            }

            // Generate between 30 and 50 events for this mission
            int numberOfEvents = ThreadLocalRandom.current().nextInt(30, 51);
            List<Evenement> missionEvents = new ArrayList<>();

            for (int i = 0; i < numberOfEvents; i++) {
                // Random date between startDate and endDate
                LocalDate eventDate = randomDateBetween(startDate, endDate);

                // Random event type
                TypeEvenement randomType = eventTypes.get(random.nextInt(eventTypes.size()));

                // Random title and description
                String title = getRandomEventTitle(eventDate, i);
                String description = getRandomEventDescription(eventDate, randomType);

                Evenement event = new Evenement();
                event.setTitre(title);
                event.setDescription(description);
                event.setDate(eventDate);
                event.setHeure(LocalTime.of(random.nextInt(9, 18), random.nextInt(0, 60)));
                event.setMission(mission);
                event.setTypeEvenement(randomType);

                missionEvents.add(event);
            }

            // Sort events by date
            missionEvents.sort(Comparator.comparing(Evenement::getDate));

            // Save all events
            evenementRepository.saveAll(missionEvents);
            totalEventCount += missionEvents.size();

            System.out.println("   ✅ Created " + missionEvents.size() + " events for mission: " + mission.getCodeMission());

            // Print some sample events
            System.out.println("      Sample events:");
            for (int i = 0; i < Math.min(5, missionEvents.size()); i++) {
                Evenement e = missionEvents.get(i);
                System.out.println("        - " + e.getDate() + " | " + e.getTitre() + " | " +
                        (e.getTypeEvenement() != null ? e.getTypeEvenement().getNom() : "N/A"));
            }
            if (missionEvents.size() > 5) {
                System.out.println("        ... and " + (missionEvents.size() - 5) + " more events");
            }
        }

        System.out.println("✅ Created " + totalEventCount + " new events total");
    }

    private LocalDate randomDateBetween(LocalDate startDate, LocalDate endDate) {
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        return LocalDate.ofEpochDay(randomDay);
    }

    private String getRandomEventTitle(LocalDate date, int index) {
        String baseTitle = eventTitles[random.nextInt(eventTitles.length)];

        // Add phase or number for variety
        if (date.isBefore(LocalDate.now())) {
            return "[Passé] " + baseTitle;
        } else if (date.isAfter(LocalDate.now())) {
            return "[Planifié] " + baseTitle;
        }
        return "[Aujourd'hui] " + baseTitle;
    }

    private String getRandomEventDescription(LocalDate date, TypeEvenement type) {
        String baseDescription = eventDescriptions[random.nextInt(eventDescriptions.length)];

        // Add status based on date
        if (date.isBefore(LocalDate.now())) {
            return baseDescription + " (Événement terminé)";
        } else if (date.isAfter(LocalDate.now())) {
            return baseDescription + " (À venir)";
        }
        return baseDescription + " (En cours aujourd'hui)";
    }

    private void assignUsersToMissions(List<Mission> allMissions) {
        if (allMissions.isEmpty()) {
            System.out.println("⚠️ No missions found to assign users to!");
            return;
        }

        // Get all users by role
        List<Employe> chefMissions = getEmployesByRole("CHEF_MISSION");
        List<Employe> chefTerrains = getEmployesByRole("CHEF_TERRAIN");
        List<Employe> gestionnaires = getEmployesByRole("GESTIONNAIRE");

        System.out.println("\n📋 Available users for assignment:");
        System.out.println("   - CHEF_MISSION available: " + chefMissions.size());
        System.out.println("   - CHEF_TERRAIN available: " + chefTerrains.size());
        System.out.println("   - GESTIONNAIRE available: " + gestionnaires.size());

        System.out.println("\n📝 Assigning users to missions...");

        int assignmentCount = 0;

        for (int i = 0; i < allMissions.size(); i++) {
            Mission mission = allMissions.get(i);

            // Get users for this mission (cycling through available users if needed)
            Employe chefMission = chefMissions.get(i % chefMissions.size());
            Employe chefTerrain = chefTerrains.get(i % chefTerrains.size());
            Employe gestionnaire = gestionnaires.get(i % gestionnaires.size());

            // Check if already assigned
            if (affectationEmployeRepository.findByEmployeAndMission(chefMission, mission).isEmpty()) {
                createAffectation(chefMission, mission, null);
                System.out.println("   ✅ Assigned CHEF_MISSION: " + chefMission.getPrenom() + " " + chefMission.getNom() +
                        " -> Mission: " + mission.getCodeMission());
                assignmentCount++;
            }

            if (affectationEmployeRepository.findByEmployeAndMission(chefTerrain, mission).isEmpty()) {
                createAffectation(chefTerrain, mission, null);
                System.out.println("   ✅ Assigned CHEF_TERRAIN: " + chefTerrain.getPrenom() + " " + chefTerrain.getNom() +
                        " -> Mission: " + mission.getCodeMission());
                assignmentCount++;
            }

            if (affectationEmployeRepository.findByEmployeAndMission(gestionnaire, mission).isEmpty()) {
                createAffectation(gestionnaire, mission, null);
                System.out.println("   ✅ Assigned GESTIONNAIRE: " + gestionnaire.getPrenom() + " " + gestionnaire.getNom() +
                        " -> Mission: " + mission.getCodeMission());
                assignmentCount++;
            }
        }

        System.out.println("\n✅ Created " + assignmentCount + " new assignments");
    }

    private void createAffectation(Employe employe, Mission mission, Equipe equipe) {
        AffectationEmploye affectation = new AffectationEmploye();
        affectation.setEmploye(employe);
        affectation.setMission(mission);
        affectation.setEquipe(equipe);
        affectation.setDateDebut(LocalDate.now());
        affectation.setDateFin(null); // No end date, currently active

        affectationEmployeRepository.save(affectation);
    }

    private List<Employe> getEmployesByRole(String roleName) {
        List<Compte> comptesWithRole = compteRepository.findAll().stream()
                .filter(compte -> compte.getRoles() != null &&
                        compte.getRoles().stream()
                                .anyMatch(role -> role.getRole().getName().equals(roleName)))
                .toList();

        List<Employe> employes = new ArrayList<>();
        for (Compte compte : comptesWithRole) {
            if (compte.getEmploye() != null) {
                employes.add(compte.getEmploye());
            }
        }

        return employes;
    }

    private void displayMissionSummary(List<Mission> allMissions) {
        System.out.println("\n📊 Mission Summary:");
        System.out.println("   Total missions: " + allMissions.size());

        long d2Count = allMissions.stream().filter(m -> m.getMethodologie() == TypeMission.D2).count();
        long d3Count = allMissions.stream().filter(m -> m.getMethodologie() == TypeMission.D3).count();

        System.out.println("   By Methodology:");
        System.out.println("      - D2: " + d2Count);
        System.out.println("      - D3: " + d3Count);

        // Events summary per mission
        System.out.println("\n📅 Events Summary:");
        for (Mission mission : allMissions) {
            List<Evenement> missionEvents = evenementRepository.findByMissionId(mission.getId());
            if (!missionEvents.isEmpty()) {
                System.out.println("   Mission " + mission.getCodeMission() + ": " + missionEvents.size() + " events");
            } else {
                System.out.println("   Mission " + mission.getCodeMission() + ": 0 events");
            }
        }

        // Count assignments per mission
        System.out.println("\n👥 Assignment Summary:");
        for (Mission mission : allMissions) {
            List<AffectationEmploye> affectations = mission.getAffectations();
            if (affectations != null) {
                System.out.println("   Mission " + mission.getCodeMission() + ": " + affectations.size() + " assignments");
            } else {
                System.out.println("   Mission " + mission.getCodeMission() + ": 0 assignments");
            }
        }

        // Upcoming events summary
        LocalDate today = LocalDate.now();
        List<Evenement> upcomingEvents = evenementRepository.findUpcomingEvents(today);
        System.out.println("\n📅 Upcoming Events (" + Math.min(20, upcomingEvents.size()) + " of " + upcomingEvents.size() + "):");
        for (Evenement event : upcomingEvents.stream().limit(20).toList()) {
            String missionCode = event.getMission() != null ? event.getMission().getCodeMission() : "N/A";
            String typeName = event.getTypeEvenement() != null ? event.getTypeEvenement().getNom() : "N/A";
            System.out.println("   - " + event.getDate() + " | " + event.getTitre() +
                    " | Mission: " + missionCode + " | Type: " + typeName);
        }

        // Past events summary
        List<Evenement> pastEvents = evenementRepository.findPastEvents(today);
        System.out.println("\n📅 Past Events (total: " + pastEvents.size() + ")");
        System.out.println("   Last 10 events:");
        for (Evenement event : pastEvents.stream().limit(10).toList()) {
            String missionCode = event.getMission() != null ? event.getMission().getCodeMission() : "N/A";
            System.out.println("   - " + event.getDate() + " | " + event.getTitre() + " | Mission: " + missionCode);
        }
    }

    private String truncateDescription(String description, int maxLength) {
        if (description.length() <= maxLength) {
            return description;
        }
        return description.substring(0, maxLength) + "...";
    }
}