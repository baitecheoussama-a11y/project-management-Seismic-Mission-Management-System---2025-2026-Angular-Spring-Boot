// src/main/java/com/pfe/webapp/config/RendementInitializer.java
package com.pfe.webapp.config;

import com.pfe.webapp.entity.*;

import com.pfe.webapp.repository.rapport.RapportRepository;
import com.pfe.webapp.repository.rapport.RendementRepository;
import com.pfe.webapp.repository.team.AffectationEquipeRepository;
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
@Order(5) // Run after EquipeInitializer
public class RendementInitializer implements CommandLineRunner {

    @Autowired
    private RendementRepository rendementRepository;

    @Autowired
    private AffectationEquipeRepository affectationEquipeRepository;

    @Autowired
    private RapportRepository rapportRepository;

    private final Random random = new Random();

    // Seismic performance metrics (units)
    private final String[] performanceUnits = {
            "km²", "m", "points", "shots", "km",
            "m²", "channels", "vibrations", "records", "sources"
    };

    // Performance metric names for better reporting
    private final String[] metricNames = {
            "Area Coverage", "Line Length", "Shot Points", "Vibrations", "Data Records",
            "Sensor Density", "Channel Count", "Vibrator Points", "Source Points", "Profile Length"
    };

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("\n🚀 Starting RendementInitializer...");
        System.out.println("   📊 Creating performance records for seismic teams");

        // Check if rendements already exist
        if (rendementRepository.count() > 0) {
            System.out.println("ℹ️ Rendements already exist in the database. Skipping creation.");
            displayRendementSummary();
            return;
        }

        // Get all affectationEquipes that have active, project, and mission
        List<AffectationEquipe> affectationEquipes = affectationEquipeRepository.findAll();

        if (affectationEquipes.isEmpty()) {
            System.out.println("⚠️ No affectationEquipes found. Please run EquipeInitializer first.");
            return;
        }

        // Filter to only those with all required fields
        List<AffectationEquipe> validAffectations = affectationEquipes.stream()
                .filter(ae -> ae.getEquipe() != null && ae.getActive() != null &&
                        ae.getProject() != null && ae.getMission() != null)
                .collect(java.util.stream.Collectors.toList());

        if (validAffectations.isEmpty()) {
            System.out.println("⚠️ No valid affectationEquipes found (missing equipe, active, project, or mission)");
            return;
        }

        System.out.println("📋 Found " + validAffectations.size() + " valid affectationEquipes");

        // Get all reports for reference
        List<Rapport> allReports = rapportRepository.findAll();
        if (allReports.isEmpty()) {
            System.out.println("⚠️ No reports found. Please run ProjectInitializer first.");
            return;
        }

        int totalRendements = 0;

        // For each affectation, create rendements
        for (AffectationEquipe affectation : validAffectations) {
            // Determine number of rendements based on project status
            Project project = affectation.getProject();
            boolean isCompleted = project.isCompleted();
            boolean isCancelled = project.isCancelled();

            int rendementCount;
            if (isCompleted || isCancelled) {
                // Completed projects: 2-5 rendements
                rendementCount = random.nextInt(4) + 2;
            } else {
                // Active projects: 3-8 rendements
                rendementCount = random.nextInt(6) + 3;
            }

            // Find reports for this project
            List<Rapport> projectReports = allReports.stream()
                    .filter(r -> r.getProject() != null && r.getProject().getId().equals(project.getId()))
                    .collect(java.util.stream.Collectors.toList());

            // Create rendements
            List<Rendement> rendements = new ArrayList<>();
            LocalDate projectStart = project.getObjectifDebut() != null ?
                    project.getObjectifDebut() : LocalDate.now().minusMonths(3);
            LocalDate projectEnd = project.getObjectifFin() != null ?
                    project.getObjectifFin() : LocalDate.now().plusMonths(3);

            for (int i = 0; i < rendementCount; i++) {
                Rendement rendement = new Rendement();

                // Random date between project start and now (or project end)
                LocalDate rendementDate = randomDateBetween(
                        projectStart,
                        isCompleted || isCancelled ? projectEnd : LocalDate.now()
                );

                // Random time (8:00 AM to 5:00 PM)
                LocalTime startTime = LocalTime.of(
                        random.nextInt(8, 10),
                        random.nextInt(0, 60)
                );
                LocalTime endTime = startTime.plusHours(random.nextInt(2, 6));

                // Calculate duration in hours
                double durationHours = endTime.toSecondOfDay() - startTime.toSecondOfDay();
                durationHours = durationHours / 3600.0; // Convert seconds to hours

                // Random performance value based on activity type
                double performanceValue = generatePerformanceValue(affectation, i);
                String unit = getPerformanceUnit(affectation);

                rendement.setDate(rendementDate);
                rendement.setHeureDebut(startTime);
                rendement.setHeureFin(endTime);
                rendement.setDureeHeures(Math.round(durationHours * 10) / 10.0); // Round to 1 decimal
                rendement.setValeurRendement(Math.round(performanceValue * 100) / 100.0); // Round to 2 decimals
                rendement.setUniteRendement(unit);
                rendement.setAffectationEquipe(affectation);

                // Assign to a report if available
                if (!projectReports.isEmpty()) {
                    Rapport randomReport = projectReports.get(random.nextInt(projectReports.size()));
                    rendement.setRapport(randomReport);
                }

                rendements.add(rendement);
            }

            // Save all rendements for this affectation
            rendementRepository.saveAll(rendements);
            totalRendements += rendements.size();

            System.out.println("   ✅ Created " + rendements.size() + " rendements for " +
                    affectation.getEquipe().getNom() + " → " +
                    affectation.getActive().getCodeActive() + " (Project: " +
                    affectation.getProject().getNom() + ")");
        }

        System.out.println("\n🎉 ==========================================");
        System.out.println("✅ RENDEMENT INITIALIZATION COMPLETED SUCCESSFULLY!");
        System.out.println("   - " + totalRendements + " Performance records created");
        System.out.println("   - For " + validAffectations.size() + " team assignments");
        System.out.println("==========================================\n");

        displayRendementSummary();
    }

    private LocalDate randomDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusMonths(3);
        if (endDate == null) endDate = LocalDate.now();

        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();

        // Ensure start <= end
        if (startEpochDay > endEpochDay) {
            long temp = startEpochDay;
            startEpochDay = endEpochDay;
            endEpochDay = temp;
        }

        long randomDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        return LocalDate.ofEpochDay(randomDay);
    }

    private double generatePerformanceValue(AffectationEquipe affectation, int index) {
        double baseValue;
        String status = affectation.getProject().calculateStatus();

        // Base value depends on activity type and project status
        TypeActivite type = affectation.getEquipe().getType();

        // Different base values for different activity types
        switch (type) {
            case TOPOGRAPHIE:
                baseValue = 50 + random.nextDouble() * 150; // 50-200
                break;
            case LAYONNAGE:
                baseValue = 100 + random.nextDouble() * 300; // 100-400
                break;
            case ENERGISREMENT:
                baseValue = 200 + random.nextDouble() * 500; // 200-700
                break;
            case POSE:
                baseValue = 30 + random.nextDouble() * 100; // 30-130
                break;
            case RAMASSAGE:
                baseValue = 80 + random.nextDouble() * 200; // 80-280
                break;
            default:
                baseValue = 100 + random.nextDouble() * 200;
        }

        // Adjust based on project status
        if ("TERMINI".equals(status)) {
            baseValue *= 1.2; // Completed projects have higher performance
        } else if ("ENRETARD".equals(status)) {
            baseValue *= 0.8; // Delayed projects have lower performance
        } else if ("ENATTENTE".equals(status)) {
            baseValue *= 0.6; // On hold projects have low performance
        } else if ("ANNULE".equals(status)) {
            baseValue *= 0.5; // Cancelled projects have very low performance
        }

        // Add some randomness
        double randomFactor = 0.8 + random.nextDouble() * 0.4; // 0.8 to 1.2
        return baseValue * randomFactor;
    }

    private String getPerformanceUnit(AffectationEquipe affectation) {
        TypeActivite type = affectation.getEquipe().getType();

        // Different units for different activity types
        switch (type) {
            case TOPOGRAPHIE:
                return "km²";
            case LAYONNAGE:
                return "km";
            case ENERGISREMENT:
                return "shots";
            case POSE:
                return "points";
            case RAMASSAGE:
                return "records";
            default:
                return performanceUnits[random.nextInt(performanceUnits.length)];
        }
    }

    private void displayRendementSummary() {
        List<Rendement> allRendements = rendementRepository.findAll();

        if (allRendements.isEmpty()) {
            System.out.println("📊 No rendements found in database.");
            return;
        }

        System.out.println("\n📊 Rendement Summary:");
        System.out.println("   Total rendements: " + allRendements.size());

        // Group by activity type
        Map<String, Long> rendementsByType = new HashMap<>();
        Map<String, Double> averageValueByType = new HashMap<>();
        Map<String, Double> totalValueByType = new HashMap<>();

        for (Rendement rendement : allRendements) {
            AffectationEquipe ae = rendement.getAffectationEquipe();
            if (ae != null && ae.getEquipe() != null) {
                String typeName = ae.getEquipe().getType().name();
                rendementsByType.merge(typeName, 1L, Long::sum);
                averageValueByType.merge(typeName, rendement.getValeurRendement(), Double::sum);
                totalValueByType.merge(typeName, rendement.getValeurRendement(), Double::sum);
            }
        }

        System.out.println("\n   By Activity Type:");
        for (Map.Entry<String, Long> entry : rendementsByType.entrySet()) {
            String type = entry.getKey();
            long count = entry.getValue();
            double avg = averageValueByType.getOrDefault(type, 0.0) / count;
            double total = totalValueByType.getOrDefault(type, 0.0);
            System.out.println("      - " + type + ": " + count + " records, Avg: " +
                    String.format("%.2f", avg) + ", Total: " + String.format("%.2f", total));
        }

        // Show sample rendements
        System.out.println("\n   Sample Rendements (last 5):");
        allRendements.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(5)
                .forEach(r -> {
                    String teamName = r.getAffectationEquipe() != null && r.getAffectationEquipe().getEquipe() != null ?
                            r.getAffectationEquipe().getEquipe().getNom() : "N/A";
                    String activeCode = r.getAffectationEquipe() != null && r.getAffectationEquipe().getActive() != null ?
                            r.getAffectationEquipe().getActive().getCodeActive() : "N/A";
                    System.out.println("      - " + r.getDate() + " | " + teamName +
                            " | " + activeCode + " | " + r.getValeurRendement() + " " + r.getUniteRendement() +
                            " | " + r.getDureeHeures() + "h");
                });
    }
}