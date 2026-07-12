package com.pfe.webapp.controller;

import com.pfe.webapp.dto.MissionDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.entity.materiel.AffectationMateriel;
import com.pfe.webapp.entity.materiel.Materiel;
import com.pfe.webapp.entity.materiel.TypeMateriel;

import com.pfe.webapp.entity.ressource.Consommation;
import com.pfe.webapp.entity.ressource.Ressource;
import com.pfe.webapp.repository.AffectationEmployeRepository;
import com.pfe.webapp.repository.CompteRepository;
import com.pfe.webapp.service.MissionService;
import com.pfe.webapp.service.AffectationMaterielService;
import com.pfe.webapp.service.MaterielService;
import com.pfe.webapp.service.ConsommationService;
import com.pfe.webapp.service.RessourceService;
import com.pfe.webapp.repository.ReparationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.pfe.webapp.entity.EtatAvancement;
import com.pfe.webapp.entity.StatusEtatAvancement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = "http://localhost:4200")
public class MissionController {

    @Autowired
    private MissionService missionService;

    @Autowired
    private AffectationMaterielService affectationMaterielService;

    @Autowired
    private MaterielService materielService;

    @Autowired
    private RessourceService ressourceService;

    @Autowired
    private ConsommationService consommationService;

    @Autowired
    private ReparationRepository reparationRepository;

    @Autowired
    private AffectationEmployeRepository affectationEmployeRepository; // أضف هذا

    @Autowired
    private CompteRepository compteRepository; // أضف هذا

    @GetMapping
    public ResponseEntity<List<MissionDTO>> getAllMissions() {
        List<Mission> missions = missionService.getAllMissions();
        List<MissionDTO> dtos = missions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionDTO> getMissionById(@PathVariable Long id) {
        Mission mission = missionService.getMissionById(id);
        return ResponseEntity.ok(convertToDTO(mission));
    }

    private MissionDTO convertToDTO(Mission mission) {
        MissionDTO dto = new MissionDTO();
        dto.setId(mission.getId());
        dto.setCodeMission(mission.getCodeMission());
        dto.setMethodologie(mission.getMethodologie());
        dto.setDescription(mission.getDescription());

        return dto;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Mission createMission(@RequestBody Mission mission) {
        return missionService.createMission(mission);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Mission updateMission(@PathVariable Long id, @RequestBody Mission mission) {
        return missionService.updateMission(id, mission);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
    }

    @GetMapping("/{id}/overview")
    public ResponseEntity<Map<String, Object>> getMissionOverview(@PathVariable Long id) {
        Mission mission = missionService.getMissionById(id);

        Map<String, Object> overview = new HashMap<>();

        // Basic mission info
        overview.put("id", mission.getId());
        overview.put("codeMission", mission.getCodeMission());
        overview.put("description", mission.getDescription() != null ? mission.getDescription() : "");

        // Current active project - filter out completed or cancelled projects
        Map<String, Object> currentProject = new HashMap<>();

        // Find active project (not cancelled AND not TERMINI/ANNULE)
        Project activeProject = null;

        if (mission.getProjects() != null && !mission.getProjects().isEmpty()) {
            for (Project project : mission.getProjects()) {
                // Skip cancelled projects
                if (Boolean.TRUE.equals(project.getAnnule())) {
                    continue;
                }

                // Get project status
                String statusCode = getProjectStatusCode(project);

                // Only consider project as active if status is NOT TERMINI or ANNULE
                boolean isActiveStatus = !"TERMINI".equals(statusCode) && !"ANNULE".equals(statusCode);

                if (isActiveStatus) {
                    activeProject = project;
                    break; // Found the active project
                }
            }
        }

        if (activeProject != null) {
            Project project = activeProject;
            currentProject.put("id", project.getId());
            currentProject.put("name", project.getNom() != null ? project.getNom() : "Current Project");
            currentProject.put("description", project.getDescription() != null ? project.getDescription() : "");
            currentProject.put("startDate", project.getObjectifDebut() != null ? project.getObjectifDebut().toString() : null);
            currentProject.put("targetEndDate", project.getObjectifFin() != null ? project.getObjectifFin().toString() : null);
            currentProject.put("progress", project.getProgression() != null ? project.getProgression() : 0);

            // Get status from etatAvancement (project-level status)
            String statusCode = getProjectStatusCode(project);

            // Store both display status and raw status code
            currentProject.put("status", getProjectDisplayStatus(statusCode));
            currentProject.put("statusCode", statusCode);

        } else {
            // No active project found
            currentProject.put("id", 0);
            currentProject.put("name", "No Active Project");
            currentProject.put("description", "No active project assigned to this mission");
            currentProject.put("startDate", null);
            currentProject.put("targetEndDate", null);
            currentProject.put("progress", 0);
            currentProject.put("status", "pending");
            currentProject.put("statusCode", "PLANIFIER");
        }
        overview.put("currentProject", currentProject);

        // Equipment statistics for the mission
        List<AffectationMateriel> affectations = affectationMaterielService.getByMissionId(id);
        Map<String, Object> equipmentStats = getEquipmentStatisticsForMission(id, affectations);
        overview.put("aggregatedEquipment", equipmentStats);

        // Resource statistics
        List<Consommation> consommations = consommationService.getByMissionId(id);
        Map<String, Object> resourceStats = getResourceStatistics(consommations);
        overview.put("aggregatedResources", resourceStats);

        // Financial statistics
        Map<String, Object> financialStats = getFinancialStatistics(consommations);
        overview.put("financial", financialStats);

        // Recent activities
        List<Map<String, Object>> recentActivities = getRecentActivities(affectations, consommations);
        overview.put("recentActivities", recentActivities);

        return ResponseEntity.ok(overview);
    }

    // Helper method to get project status code
    private String getProjectStatusCode(Project project) {
        if (project == null) return "PLANIFIER";

        // Get status from etatAvancement (project-level status where active is null)
        if (project.getEtatAvancements() != null && !project.getEtatAvancements().isEmpty()) {
            Optional<EtatAvancement> projectStatus = project.getEtatAvancements().stream()
                    .filter(e -> e.getActive() == null)
                    .findFirst();

            if (projectStatus.isPresent() && projectStatus.get().getStatus() != null) {
                return projectStatus.get().getStatus().name();
            }
        }

        return "PLANIFIER";
    }
    // Helper method to convert status code to display format
    private String getProjectDisplayStatus(String statusCode) {
        if (statusCode == null) return "pending";

        switch (statusCode) {
            case "PLANIFIER":
                return "planned";
            case "ENCOURS":
                return "in-progress";
            case "ENATTENTE":
                return "on-hold";
            case "ENRETARD":
                return "delayed";
            case "TERMINI":
                return "completed";
            case "ANNULE":
                return "cancelled";
            default:
                return "pending";
        }
    }
    // Update the getProjectStatus method to use EtatAvancement
    private String getProjectStatus(Project project) {
        if (project == null) return "pending";

        // Get the project's etatAvancement (project-level status)
        if (project.getEtatAvancements() != null && !project.getEtatAvancements().isEmpty()) {
            // Get the first etatAvancement (project status)
            EtatAvancement etat = project.getEtatAvancements().get(0);
            StatusEtatAvancement status = etat.getStatus();

            if (status != null) {
                switch (status) {
                    case PLANIFIER:
                        return "planned";
                    case ENCOURS:
                        return "in-progress";
                    case ENATTENTE:
                        return "on-hold";
                    case ENRETARD:
                        return "delayed";
                    case TERMINI:
                        return "completed";
                    case ANNULE:
                        return "cancelled";
                    default:
                        return "pending";
                }
            }
        }

        // Fallback: calculate based on progression percentage
        Integer progression = project.getProgression();
        if (progression == null) return "pending";
        if (progression >= 100) return "completed";
        if (progression >= 70) return "on-track";
        if (progression >= 40) return "at-risk";
        return "delayed";
    }
    private Map<String, Object> getEquipmentStatisticsForMission(Long missionId, List<AffectationMateriel> affectations) {
        Map<String, Object> stats = new HashMap<>();

        int totalEquipment = affectations.size();
        List<Map<String, Object>> equipmentList = new ArrayList<>();
        Map<String, Integer> byStatus = new HashMap<>();

        byStatus.put("good", 0);
        byStatus.put("broken", 0);
        byStatus.put("inRepair", 0);

        Map<String, Integer> typeCount = new HashMap<>();

        for (AffectationMateriel affectation : affectations) {
            Materiel materiel = affectation.getMateriel();
            if (materiel != null) {
                // Each affectation represents one equipment unit

                // Count broken equipment for this affectation
                Integer brokenCount = getBrokenCountForAffectation(affectation.getIdAffectation(), missionId);
                Integer inRepairCount = getInRepairCountForAffectation(affectation.getIdAffectation(), missionId);

                int broken = brokenCount != null ? brokenCount : 0;
                int inRepair = inRepairCount != null ? inRepairCount : 0;
                int good = 1 - broken - inRepair;
                if (good < 0) good = 0;

                // Update statistics by status
                byStatus.put("good", byStatus.get("good") + good);
                byStatus.put("broken", byStatus.get("broken") + broken);
                byStatus.put("inRepair", byStatus.get("inRepair") + inRepair);

                // By type
                TypeMateriel type = materiel.getTypeMateriel();
                String typeName = type != null ? type.getLibelle() : "Other";
                typeCount.put(typeName, typeCount.getOrDefault(typeName, 0) + 1);
            }
        }

        // Convert type count to list
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            Map<String, Object> equipmentItem = new HashMap<>();
            equipmentItem.put("type", entry.getKey());
            equipmentItem.put("count", entry.getValue());
            equipmentItem.put("icon", getEquipmentIcon(entry.getKey()));
            equipmentItem.put("color", getEquipmentColor(entry.getKey()));
            equipmentList.add(equipmentItem);
        }

        stats.put("total", totalEquipment);
        stats.put("byType", equipmentList);
        stats.put("byStatus", byStatus);

        return stats;
    }

    private Integer getBrokenCountForAffectation(Long affectationId, Long missionId) {
        try {
            return reparationRepository.countBrokenByAffectationIdAndMissionId(affectationId, missionId);
        } catch (Exception e) {
            return 0;
        }
    }

    private Integer getInRepairCountForAffectation(Long affectationId, Long missionId) {
        try {
            return reparationRepository.countInRepairByAffectationIdAndMissionId(affectationId, missionId);
        } catch (Exception e) {
            return 0;
        }
    }

    private Map<String, Object> getResourceStatistics(List<Consommation> consommations) {
        Map<String, Object> stats = new HashMap<>();

        double totalConsumed = 0;
        List<Map<String, Object>> resourcesList = new ArrayList<>();
        Map<String, Double> resourceConsumption = new HashMap<>();

        for (Consommation consommation : consommations) {
            Ressource ressource = consommation.getRessource();
            if (ressource != null) {
                double consumed = consommation.getValeur() != null ? consommation.getValeur() : 0;
                totalConsumed += consumed;

                String name = ressource.getTitre();
                resourceConsumption.put(name, resourceConsumption.getOrDefault(name, 0.0) + consumed);
            }
        }

        // Get allocated resources (from stock)
        List<Ressource> allResources = ressourceService.getAllRessources();
        double totalAllocated = 0;

        for (Ressource res : allResources) {
            double allocated = res.getQuantite() != null ? res.getQuantite() : 0;
            totalAllocated += allocated;

            Map<String, Object> resourceItem = new HashMap<>();
            resourceItem.put("name", res.getTitre());
            resourceItem.put("allocated", allocated);
            resourceItem.put("consumed", resourceConsumption.getOrDefault(res.getTitre(), 0.0));
            resourceItem.put("unit", res.getUnite() != null ? res.getUnite() : "units");
            resourcesList.add(resourceItem);
        }

        stats.put("total", (int) totalAllocated);
        stats.put("consumed", (int) totalConsumed);
        stats.put("remaining", (int) (totalAllocated - totalConsumed));
        stats.put("byCategory", resourcesList);
        stats.put("totalCost", totalConsumed * 100);

        return stats;
    }

    private Map<String, Object> getFinancialStatistics(List<Consommation> consommations) {
        Map<String, Object> stats = new HashMap<>();

        double totalBudget = 500000;
        double totalSpent = 0;
        Map<String, Double> breakdown = new HashMap<>();

        for (Consommation consommation : consommations) {
            Ressource ressource = consommation.getRessource();
            if (ressource != null && ressource.getCout() != null && consommation.getValeur() != null) {
                double cost = ressource.getCout() * consommation.getValeur();
                totalSpent += cost;

                String category = ressource.getTypeRessource() != null ?
                        ressource.getTypeRessource().getNom() : "Other";
                breakdown.put(category, breakdown.getOrDefault(category, 0.0) + cost);
            }
        }

        List<Map<String, Object>> breakdownList = new ArrayList<>();
        String[] colors = {"#3b82f6", "#f59e0b", "#10b981", "#8b5cf6", "#ef4444", "#06b6d4"};
        int colorIndex = 0;

        for (Map.Entry<String, Double> entry : breakdown.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("category", entry.getKey());
            item.put("amount", entry.getValue());
            item.put("color", colors[colorIndex % colors.length]);
            breakdownList.add(item);
            colorIndex++;
        }

        stats.put("budget", totalBudget);
        stats.put("spent", totalSpent);
        stats.put("remaining", totalBudget - totalSpent);
        stats.put("breakdown", breakdownList);

        return stats;
    }

    private List<Map<String, Object>> getRecentActivities(List<AffectationMateriel> affectations, List<Consommation> consommations) {
        List<Map<String, Object>> activities = new ArrayList<>();

        // Add equipment assignments (last 2)
        int equipmentCount = 0;
        for (AffectationMateriel affectation : affectations) {
            if (equipmentCount >= 2) break;
            if (affectation.getDateDebut() != null) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("icon", "fas fa-microchip");
                activity.put("text", "Equipment unit assigned to mission");
                activity.put("time", "Recently");
                activity.put("color", "#3b82f6");
                activities.add(activity);
                equipmentCount++;
            }
        }

        // Add resource consumptions (last 3)
        int consumptionCount = 0;
        for (Consommation consommation : consommations) {
            if (consumptionCount >= 3) break;
            if (consommation.getDate() != null) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("icon", "fas fa-chart-line");
                activity.put("text", consommation.getValeur() + " units of resources consumed");
                activity.put("time", "Recently");
                activity.put("color", "#f59e0b");
                activities.add(activity);
                consumptionCount++;
            }
        }

        return activities.stream().limit(5).collect(Collectors.toList());
    }

    private String getEquipmentIcon(String type) {
        Map<String, String> iconMap = new HashMap<>();
        iconMap.put("server", "fas fa-server");
        iconMap.put("workstation", "fas fa-laptop");
        iconMap.put("laptop", "fas fa-laptop");
        iconMap.put("network", "fas fa-wifi");
        iconMap.put("wifi", "fas fa-wifi");
        iconMap.put("peripheral", "fas fa-print");
        iconMap.put("printer", "fas fa-print");

        String lowerType = type.toLowerCase();
        for (Map.Entry<String, String> entry : iconMap.entrySet()) {
            if (lowerType.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "fas fa-microchip";
    }

    private String getEquipmentColor(String type) {
        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("server", "#3b82f6");
        colorMap.put("workstation", "#10b981");
        colorMap.put("laptop", "#10b981");
        colorMap.put("network", "#f59e0b");
        colorMap.put("wifi", "#f59e0b");
        colorMap.put("peripheral", "#8b5cf6");
        colorMap.put("printer", "#8b5cf6");

        String lowerType = type.toLowerCase();
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            if (lowerType.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "#6b7280";
    }

    // controller/MissionController.java (أضف هذه endpoints)

    // Get current active mission for the logged-in user

    @GetMapping("/my-mission")
    public ResponseEntity<?> getMyCurrentMission(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            // البحث عن الحساب
            Compte compte = compteRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Employe employe = compte.getEmploye();
            if (employe == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "No employee associated with this account"));
            }

            // البحث عن المهمة النشطة للموظف
            LocalDate today = LocalDate.now();
            Optional<AffectationEmploye> activeAffectation = affectationEmployeRepository
                    .findActiveMissionByEmployeId(employe.getId(), today);

            if (activeAffectation.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "You are not currently assigned to any active mission"));
            }

            AffectationEmploye affectation = activeAffectation.get();
            Mission currentMission = affectation.getMission();

            Map<String, Object> response = new HashMap<>();
            response.put("missionId", currentMission.getId());
            response.put("hasAccess", true);
            response.put("missionCode", currentMission.getCodeMission());
            response.put("dateDebut", affectation.getDateDebut());
            response.put("dateFin", affectation.getDateFin()); // يمكن أن يكون null
            response.put("isActive", affectation.getDateFin() == null || !affectation.getDateFin().isBefore(today));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{missionId}/check-access")
    public ResponseEntity<?> checkMissionAccess(@PathVariable Long missionId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Compte compte = compteRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Employe employe = compte.getEmploye();
            if (employe == null) {
                return ResponseEntity.ok(Map.of("hasAccess", false));
            }

            LocalDate today = LocalDate.now();
            Optional<AffectationEmploye> affectation = affectationEmployeRepository
                    .findByMissionIdAndEmployeIdAndDateFinAfterOrDateFinNull(missionId, employe.getId(), today);

            boolean hasAccess = affectation.isPresent();

            return ResponseEntity.ok(Map.of("hasAccess", hasAccess));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("hasAccess", false, "error", e.getMessage()));
        }
    }

}