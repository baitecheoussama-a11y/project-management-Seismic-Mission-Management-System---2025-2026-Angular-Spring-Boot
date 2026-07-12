// src/main/java/com/pfe/webapp/config/EquipeInitializer.java
package com.pfe.webapp.config;

import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.EquipeRepository;
import com.pfe.webapp.repository.ProjectRepository;
import com.pfe.webapp.repository.team.ActiveRepository;
import com.pfe.webapp.repository.team.AffectationEquipeRepository;
import com.pfe.webapp.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Order(4)
public class EquipeInitializer implements CommandLineRunner {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private ActiveRepository activeRepository;

    @Autowired
    private AffectationEquipeRepository affectationEquipeRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private final Random random = new Random();

    // ✅ SEISMIC EXPLORATION TEAM NAMES (English)
    private final String[] teamNames = {
            // Topography Teams
            "Topography Team Alpha",
            "Topography Team Beta",
            "Topography Team Gamma",
            "Topography Team Delta",

            // Line Cutting Teams
            "Line Cutting Team North",
            "Line Cutting Team South",
            "Line Cutting Team East",
            "Line Cutting Team West",

            // Energizing Teams
            "Energizing Team Primary",
            "Energizing Team Secondary",
            "Energizing Team Tertiary",

            // Sensor Placement Teams
            "Sensor Placement Team A",
            "Sensor Placement Team B",
            "Sensor Placement Team C",
            "Cable Placement Team",

            // Collection Teams
            "Collection Team North",
            "Collection Team South",
            "Collection Team East",
            "Collection Team West",

            // Specialized Teams
            "3D Seismic Team",
            "4D Seismic Team",
            "Data Processing Team",
            "Seismic Acquisition Team",
            "Seismic Interpretation Team"
    };

    // ✅ SEISMIC ACTIVITY TYPES
    private final TypeActivite[] teamTypes = {
            TypeActivite.TOPOGRAPHIE,
            TypeActivite.LAYONNAGE,
            TypeActivite.ENERGISREMENT,
            TypeActivite.POSE,
            TypeActivite.RAMASSAGE
    };

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("\n🚀 Starting EquipeInitializer...");
        System.out.println("   🌍 Seismic Exploration Teams (Oil & Gas)");

        if (equipeRepository.count() >= 20) {
            System.out.println("ℹ️ 20 teams already exist, skipping creation");
            displayTeamSummary();
            return;
        }

        List<Mission> missions = missionRepository.findAll();
        if (missions.isEmpty()) {
            System.out.println("⚠️ No missions found. Please run MissionInitializer first.");
            return;
        }

        List<Active> allActivities = activeRepository.findAll();
        if (allActivities.isEmpty()) {
            System.out.println("⚠️ No activities found. Please run ActiveInitializer first.");
            return;
        }

        // Get all projects grouped by mission
        Map<Long, List<Project>> projectsByMission = new HashMap<>();
        for (Mission mission : missions) {
            List<Project> projects = projectRepository.findByMissionId(mission.getId());
            projectsByMission.put(mission.getId(), projects);
        }

        System.out.println("📋 Found " + missions.size() + " missions");
        System.out.println("📋 Found " + allActivities.size() + " activities");

        List<Equipe> teams = createTeams();

        // Assign activities to teams based on project status
        assignActivitiesToTeamsByProjectStatus(teams, allActivities, missions, projectsByMission);

        // Assign teams to missions (WITH projects and activities)
        assignTeamsToMissionsWithProjects(teams, missions, projectsByMission);

        displayTeamSummary();

        System.out.println("\n🎉 ==========================================");
        System.out.println("✅ EQUIPES INITIALIZATION COMPLETED SUCCESSFULLY!");
        System.out.println("   - " + teams.size() + " Teams created");
        System.out.println("   - Each team has unique activities assigned to projects");
        System.out.println("   - Teams assigned to missions with projects");
        System.out.println("==========================================\n");
    }

    private List<Equipe> createTeams() {
        List<Equipe> teams = new ArrayList<>();
        int teamCount = Math.min(20, teamNames.length);

        for (int i = 0; i < teamCount; i++) {
            Equipe equipe = new Equipe();
            String code = String.format("EQ-%03d", i + 1);
            String name = teamNames[i] + " (" + code + ")";
            equipe.setNom(name);

            // Assign appropriate type based on team name
            TypeActivite type = assignTypeBasedOnName(teamNames[i]);
            equipe.setType(type);

            Equipe savedEquipe = equipeRepository.save(equipe);
            teams.add(savedEquipe);
            System.out.println("   ✅ Created team: " + savedEquipe.getNom() + " | Type: " + savedEquipe.getType());
        }

        System.out.println("\n✅ Created " + teams.size() + " teams");
        return teams;
    }

    private TypeActivite assignTypeBasedOnName(String name) {
        if (name.contains("Topography")) return TypeActivite.TOPOGRAPHIE;
        if (name.contains("Line Cutting")) return TypeActivite.LAYONNAGE;
        if (name.contains("Energizing")) return TypeActivite.ENERGISREMENT;
        if (name.contains("Sensor") || name.contains("Cable Placement")) return TypeActivite.POSE;
        if (name.contains("Collection")) return TypeActivite.RAMASSAGE;
        if (name.contains("Seismic") || name.contains("Acquisition") ||
                name.contains("Interpretation") || name.contains("Processing")) {
            TypeActivite[] types = TypeActivite.values();
            return types[random.nextInt(types.length)];
        }
        return TypeActivite.TOPOGRAPHIE; // default
    }

    private void assignActivitiesToTeamsByProjectStatus(
            List<Equipe> teams,
            List<Active> allActivities,
            List<Mission> missions,
            Map<Long, List<Project>> projectsByMission) {

        System.out.println("\n📝 Assigning activities to teams based on project status...");

        // Shuffle activities for random distribution
        List<Active> shuffledActivities = new ArrayList<>(allActivities);
        Collections.shuffle(shuffledActivities);

        Set<Long> usedActivityIds = new HashSet<>();
        int activityIndex = 0;

        // For each mission, get its projects
        for (Mission mission : missions) {
            List<Project> missionProjects = projectsByMission.getOrDefault(mission.getId(), new ArrayList<>());

            if (missionProjects.isEmpty()) {
                System.out.println("   ⚠️ No projects for mission: " + mission.getCodeMission());
                continue;
            }

            // For each project, assign activities
            for (Project project : missionProjects) {
                // Determine how many activities based on project status
                int activityCount;
                boolean isCompleted = project.isCompleted();
                boolean isCancelled = project.isCancelled();

                if (isCompleted || isCancelled) {
                    activityCount = random.nextInt(3) + 2; // 2-4 activities
                } else {
                    activityCount = random.nextInt(5) + 3; // 3-7 activities
                }

                // Get random team for this project (prefer team with matching type)
                Equipe team = getMatchingTeamForProject(teams, project);
                if (team == null) {
                    team = teams.get(random.nextInt(teams.size()));
                }

                // Assign unique activities to this project
                List<Active> projectActivities = new ArrayList<>();
                for (int i = 0; i < Math.min(activityCount, shuffledActivities.size() - usedActivityIds.size()); i++) {
                    Active activity = null;
                    int attempts = 0;
                    while (activity == null && attempts < shuffledActivities.size()) {
                        if (activityIndex >= shuffledActivities.size()) {
                            activityIndex = 0;
                        }
                        Active candidate = shuffledActivities.get(activityIndex);
                        if (!usedActivityIds.contains(candidate.getId())) {
                            activity = candidate;
                            usedActivityIds.add(candidate.getId());
                        }
                        activityIndex++;
                        attempts++;
                    }

                    if (activity != null) {
                        projectActivities.add(activity);
                        createAffectationEquipeWithProject(team, activity, project, isCompleted || isCancelled);
                    }
                }

                System.out.println("   ✅ Project " + project.getNom() + " (" + project.calculateStatus() +
                        ") assigned " + projectActivities.size() + " activities");
            }
        }

        System.out.println("✅ Total activities used: " + usedActivityIds.size() + " out of " + allActivities.size());
    }

    private Equipe getMatchingTeamForProject(List<Equipe> teams, Project project) {
        // Try to find a team with type matching the project's mission methodology
        if (project.getMission() != null) {
            TypeMission missionType = project.getMission().getMethodologie();
            List<Equipe> matchingTeams = new ArrayList<>();

            if (missionType == TypeMission.D2) {
                // D2 projects: Topography + Line Cutting teams
                matchingTeams = teams.stream()
                        .filter(t -> t.getType() == TypeActivite.TOPOGRAPHIE ||
                                t.getType() == TypeActivite.LAYONNAGE)
                        .collect(Collectors.toList());
            } else if (missionType == TypeMission.D3) {
                // D3 projects: Sensor Placement + Collection teams
                matchingTeams = teams.stream()
                        .filter(t -> t.getType() == TypeActivite.POSE ||
                                t.getType() == TypeActivite.RAMASSAGE)
                        .collect(Collectors.toList());
            }

            if (!matchingTeams.isEmpty()) {
                return matchingTeams.get(random.nextInt(matchingTeams.size()));
            }
        }
        return null;
    }

    private void createAffectationEquipeWithProject(Equipe equipe, Active active, Project project, boolean isCompleted) {
        AffectationEquipe affectation = new AffectationEquipe();

        LocalDate startDate;
        LocalDate endDate;
        LocalDate dateStartReelle;
        LocalDate dateFinReelle;

        if (isCompleted || project.isCancelled()) {
            // Completed/Cancelled project: all dates in the past
            startDate = project.getObjectifDebut() != null ?
                    project.getObjectifDebut() : LocalDate.now().minusMonths(6);
            endDate = project.getObjectifFin() != null ?
                    project.getObjectifFin() : startDate.plusMonths(3);
            dateStartReelle = startDate.plusDays(random.nextInt(10));
            dateFinReelle = endDate.minusDays(random.nextInt(10));
        } else {
            // Active project: mixed dates
            startDate = project.getObjectifDebut() != null ?
                    project.getObjectifDebut() : LocalDate.now().minusMonths(2);
            endDate = project.getObjectifFin() != null ?
                    project.getObjectifFin() : LocalDate.now().plusMonths(4);

            // Random status for this activity
            String status = getRandomActivityStatus();

            if ("TERMINI".equals(status)) {
                dateStartReelle = startDate.plusDays(random.nextInt(10));
                dateFinReelle = endDate.minusDays(random.nextInt(10));
            } else if ("ENCOURS".equals(status) || "ENRETARD".equals(status)) {
                dateStartReelle = startDate.plusDays(random.nextInt(10));
                dateFinReelle = null;
            } else if ("ENATTENTE".equals(status)) {
                dateStartReelle = LocalDate.now().plusMonths(random.nextInt(3) + 1);
                dateFinReelle = null;
            } else {
                // PLANIFIER
                dateStartReelle = null;
                dateFinReelle = null;
            }
        }

        // ✅ SET ALL FIELDS
        affectation.setEquipe(equipe);
        affectation.setActive(active);
        affectation.setProject(project);
        affectation.setMission(project.getMission());
        affectation.setDateDebut(startDate);
        affectation.setDateFin(endDate);
        affectation.setDateStartReelle(dateStartReelle);
        affectation.setDateFinReelle(dateFinReelle);
        affectation.setOrdre(random.nextInt(10) + 1);

        affectationEquipeRepository.save(affectation);
    }

    private String getRandomActivityStatus() {
        String[] statuses = {"PLANIFIER", "ENCOURS", "ENRETARD", "ENATTENTE", "TERMINI"};
        int randomValue = random.nextInt(100);
        if (randomValue < 30) return "PLANIFIER";
        if (randomValue < 60) return "ENCOURS";
        if (randomValue < 75) return "ENRETARD";
        if (randomValue < 90) return "ENATTENTE";
        return "TERMINI";
    }

    private void assignTeamsToMissionsWithProjects(
            List<Equipe> teams,
            List<Mission> missions,
            Map<Long, List<Project>> projectsByMission) {

        System.out.println("\n📝 Assigning teams to missions with projects...");

        for (Mission mission : missions) {
            List<Project> missionProjects = projectsByMission.getOrDefault(mission.getId(), new ArrayList<>());

            if (missionProjects.isEmpty()) {
                System.out.println("   ⚠️ No projects for mission: " + mission.getCodeMission());
                continue;
            }

            // Get random team
            Equipe team = teams.get(random.nextInt(teams.size()));

            // Assign team to EACH project in the mission
            for (Project project : missionProjects) {
                List<AffectationEquipe> existingAssignments = affectationEquipeRepository
                        .findByProjectId(project.getId());

                if (!existingAssignments.isEmpty()) {
                    continue;
                }

                // Get an active for this project
                List<AffectationEquipe> projectActivities = affectationEquipeRepository
                        .findByProjectId(project.getId());

                Active active = null;
                if (!projectActivities.isEmpty()) {
                    active = projectActivities.get(0).getActive();
                } else {
                    List<Active> allActives = activeRepository.findAll();
                    if (!allActives.isEmpty()) {
                        active = allActives.get(random.nextInt(allActives.size()));
                    }
                }

                if (active != null) {
                    // ✅ CREATE COMPLETE AFFECTATION WITH ALL FIELDS
                    AffectationEquipe missionAffectation = new AffectationEquipe();
                    missionAffectation.setEquipe(team);
                    missionAffectation.setActive(active);
                    missionAffectation.setProject(project);
                    missionAffectation.setMission(mission);
                    missionAffectation.setDateDebut(LocalDate.now().minusMonths(random.nextInt(3)));
                    missionAffectation.setDateFin(LocalDate.now().plusMonths(random.nextInt(6) + 3));
                    missionAffectation.setDateStartReelle(LocalDate.now().minusMonths(random.nextInt(2)));
                    missionAffectation.setDateFinReelle(null);
                    missionAffectation.setOrdre(random.nextInt(10) + 1);

                    affectationEquipeRepository.save(missionAffectation);
                }
            }

            List<AffectationEquipe> missionAssignments = affectationEquipeRepository
                    .findByMissionId(mission.getId());
            long uniqueProjects = missionAssignments.stream()
                    .map(a -> a.getProject() != null ? a.getProject().getId() : null)
                    .filter(id -> id != null)
                    .distinct()
                    .count();

            System.out.println("   ✅ Mission " + mission.getCodeMission() + " assigned teams to " +
                    uniqueProjects + " projects");
        }
    }

    private void displayTeamSummary() {
        List<Equipe> allTeams = equipeRepository.findAll();

        System.out.println("\n📊 Team Summary:");
        System.out.println("   Total teams: " + allTeams.size());

        Map<TypeActivite, Long> typeCounts = allTeams.stream()
                .collect(Collectors.groupingBy(Equipe::getType, Collectors.counting()));

        System.out.println("   By Type:");
        for (Map.Entry<TypeActivite, Long> entry : typeCounts.entrySet()) {
            System.out.println("      - " + entry.getKey() + ": " + entry.getValue() + " teams");
        }

        System.out.println("\n📋 AffectationEquipe Summary:");
        List<AffectationEquipe> allAssignments = affectationEquipeRepository.findAll();

        long withAllFields = allAssignments.stream()
                .filter(a -> a.getEquipe() != null && a.getActive() != null &&
                        a.getProject() != null && a.getMission() != null)
                .count();

        System.out.println("   Total assignments: " + allAssignments.size());
        System.out.println("   With all fields (equipe+active+project+mission): " + withAllFields);

        if (allAssignments.size() > 0 && withAllFields < allAssignments.size()) {
            System.out.println("   ⚠️ Some assignments are missing fields!");
        }

        // Show teams by type
        System.out.println("\n🏷️ Team Details:");
        for (Equipe team : allTeams) {
            List<AffectationEquipe> assignments = affectationEquipeRepository.findByEquipeId(team.getId());
            long projectCount = assignments.stream()
                    .filter(a -> a.getProject() != null)
                    .map(a -> a.getProject().getId())
                    .distinct()
                    .count();
            System.out.println("   - " + team.getNom() + " | Type: " + team.getType() +
                    " | " + projectCount + " projects");
        }
    }
}