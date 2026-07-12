package com.pfe.webapp.config;

import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.entity.ressource.Consommation;
import com.pfe.webapp.entity.ressource.Ressource;
import com.pfe.webapp.repository.MissionRepository;
import com.pfe.webapp.repository.ressource.ConsommationRepository;
import com.pfe.webapp.repository.ressource.RessourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Order(3) // Run after MissionInitializer and EquipmentDataInitializer
public class ConsommationInitializer implements CommandLineRunner {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private RessourceRepository ressourceRepository;

    @Autowired
    private ConsommationRepository consommationRepository;

    private final Random random = ThreadLocalRandom.current();

    // Consumption descriptions (resume)
    private final String[] descriptions = {
            "Seismic acquisition operations - Daily consumption",
            "Drilling operations - Material usage",
            "Field logistics - Supply consumption",
            "Equipment maintenance - Parts replacement",
            "Camp operations - Daily supplies",
            "Transportation - Fuel consumption",
            "Office administration - Stationery usage",
            "Safety equipment usage - PPE distribution",
            "Communication systems - Battery usage",
            "Power generation - Fuel consumption",
            "Surveying operations - Equipment usage",
            "Data processing - Storage usage",
            "Equipment setup - Material consumption",
            "Emergency response - Resource allocation",
            "Training exercises - Material usage",
            "Routine operation - Standard consumption",
            "Scheduled maintenance - Parts replacement",
            "New equipment setup - Initial materials",
            "Special project - Resource allocation",
            "Daily operations - Regular consumption",
            "Weekly replenishment - Stock usage",
            "Preventive maintenance - Material usage",
            "Urgent request - Additional resources",
            "Operational support - Consumables",
            "Field survey - Equipment usage"
    };

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("\n🚀 Starting ConsommationInitializer...");

        List<Mission> missions = missionRepository.findAll();
        if (missions.isEmpty()) {
            System.out.println("⚠️ No missions found! Please run MissionInitializer first.");
            return;
        }

        List<Ressource> ressources = ressourceRepository.findAll();
        if (ressources.isEmpty()) {
            System.out.println("⚠️ No resources found! Please run EquipmentDataInitializer first.");
            return;
        }

        System.out.println("📊 Found " + missions.size() + " missions and " + ressources.size() + " resources");

        // Check if consumptions already exist
        long existingConsumptions = consommationRepository.count();
        if (existingConsumptions > 0) {
            System.out.println("ℹ️ " + existingConsumptions + " consumptions already exist. Skipping creation.");
            return;
        }

        System.out.println("\n📝 Creating consumptions for all missions...");
        int totalConsumptions = 0;

        for (Mission mission : missions) {
            // Generate 5-20 consumptions per mission
            int countPerMission = random.nextInt(5, 21);
            List<Consommation> missionConsumptions = new ArrayList<>();

            // Select random resources for this mission (3-8 different resources)
            int resourceCount = random.nextInt(3, 9);
            List<Ressource> selectedRessources = selectRandomRessources(ressources, resourceCount);

            for (int i = 0; i < countPerMission; i++) {
                // Pick a random resource from selected ones
                Ressource ressource = selectedRessources.get(random.nextInt(selectedRessources.size()));

                // Generate random consumption amount (5-200 units)
                double valeur = 5 + random.nextDouble() * 195;

                // Random date within the last 6 months
                LocalDate date = generateRandomDate();

                // Random description
                String resume = descriptions[random.nextInt(descriptions.length)];

                Consommation consommation = new Consommation();
                consommation.setDate(date);
                consommation.setValeur(valeur);
                consommation.setResume(resume);
                consommation.setMission(mission);
                consommation.setRessource(ressource);

                missionConsumptions.add(consommation);
            }

            // Sort by date
            missionConsumptions.sort(Comparator.comparing(Consommation::getDate));

            // Save all consumptions for this mission
            consommationRepository.saveAll(missionConsumptions);
            totalConsumptions += missionConsumptions.size();

            System.out.println("   ✅ Created " + missionConsumptions.size() + " consumptions for mission: " + mission.getCodeMission());

            // Print sample
            System.out.println("      Sample consumptions:");
            for (int i = 0; i < Math.min(3, missionConsumptions.size()); i++) {
                Consommation c = missionConsumptions.get(i);
                System.out.println("        - " + c.getDate() + " | " +
                        (c.getRessource() != null ? c.getRessource().getTitre() : "N/A") +
                        " | " + String.format("%.1f", c.getValeur()) + " units | " +
                        (c.getResume() != null ? c.getResume().substring(0, Math.min(30, c.getResume().length())) + "..." : "N/A"));
            }
            if (missionConsumptions.size() > 3) {
                System.out.println("        ... and " + (missionConsumptions.size() - 3) + " more consumptions");
            }
        }

        System.out.println("\n✅ Created " + totalConsumptions + " consumptions total");

        // Display summary by mission
        displayConsumptionSummary(missions);
    }

    private List<Ressource> selectRandomRessources(List<Ressource> allRessources, int count) {
        List<Ressource> shuffled = new ArrayList<>(allRessources);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    private LocalDate generateRandomDate() {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusMonths(6); // Start 6 months ago
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = now.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        return LocalDate.ofEpochDay(randomDay);
    }

    private void displayConsumptionSummary(List<Mission> missions) {
        System.out.println("\n📊 Consumption Summary:");

        double totalConsumption = 0;
        Map<String, Double> consumptionByMission = new LinkedHashMap<>();
        Map<String, Double> consumptionByResource = new LinkedHashMap<>();

        for (Mission mission : missions) {
            List<Consommation> missionConsumptions = consommationRepository.findByMissionId(mission.getId());
            if (missionConsumptions.isEmpty()) continue;

            double missionTotal = missionConsumptions.stream()
                    .mapToDouble(Consommation::getValeur)
                    .sum();

            consumptionByMission.put(mission.getCodeMission(), missionTotal);
            totalConsumption += missionTotal;

            // Calculate per resource
            for (Consommation c : missionConsumptions) {
                if (c.getRessource() != null) {
                    String resourceName = c.getRessource().getTitre();
                    consumptionByResource.put(resourceName,
                            consumptionByResource.getOrDefault(resourceName, 0.0) + c.getValeur());
                }
            }
        }

        // Display by mission
        System.out.println("\n   By Mission:");
        consumptionByMission.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    System.out.println("      " + entry.getKey() + ": " + String.format("%.1f", entry.getValue()) + " units");
                });

        System.out.println("\n   Total Consumption: " + String.format("%.1f", totalConsumption) + " units");

        // Display top 5 resources
        System.out.println("\n   Top 5 Consumed Resources:");
        consumptionByResource.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    System.out.println("      " + entry.getKey() + ": " + String.format("%.1f", entry.getValue()) + " units");
                });

        // Monthly trend
        System.out.println("\n   Monthly Trend:");
        Map<String, Double> monthlyConsumption = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            LocalDate monthStart = now.minusMonths(i);
            String monthKey = monthStart.getYear() + "-" + String.format("%02d", monthStart.getMonthValue());

            double monthlyTotal = 0;
            for (Mission mission : missions) {
                List<Consommation> consumptions = consommationRepository.findByMissionId(mission.getId());
                for (Consommation c : consumptions) {
                    if (c.getDate().getYear() == monthStart.getYear() &&
                            c.getDate().getMonthValue() == monthStart.getMonthValue()) {
                        monthlyTotal += c.getValeur();
                    }
                }
            }
            monthlyConsumption.put(monthKey, monthlyTotal);
        }

        monthlyConsumption.forEach((month, total) -> {
            System.out.println("      " + month + ": " + String.format("%.1f", total) + " units");
        });
    }
}