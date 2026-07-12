package com.pfe.webapp.config;

import com.pfe.webapp.entity.Active;
import com.pfe.webapp.repository.team.ActiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Order(3) // Run after MissionInitializer
public class ActiveInitializer implements CommandLineRunner {

    @Autowired
    private ActiveRepository activeRepository;

    private final Random random = new Random();

    // Activity objectives
    private final String[] OBJECTIVES = {
            "Increase productivity", "Reduce costs", "Improve quality", "Enhance customer satisfaction",
            "Optimize processes", "Digital transformation", "Training and development", "Risk management",
            "Innovation implementation", "Market expansion", "Team collaboration", "Resource optimization",
            "Performance monitoring", "Security enhancement", "Compliance improvement", "Sustainability goals",
            "Communication improvement", "Data analysis", "Automation implementation", "Customer retention"
    };

    // Activity descriptions
    private final String[] DESCRIPTIONS = {
            "Implementation of new workflows to streamline operations",
            "Development of automated reporting system",
            "Creation of training materials for team members",
            "Integration of customer feedback into product development",
            "Regular team meetings and progress tracking",
            "Documentation of standard operating procedures",
            "Setup of performance metrics dashboard",
            "Conduct regular audits and quality checks",
            "Organize workshops for skill development",
            "Implement agile methodologies across teams",
            "Develop disaster recovery protocols",
            "Create knowledge base for common issues",
            "Establish communication channels between departments",
            "Monitor key performance indicators",
            "Conduct market research and analysis",
            "Implement security best practices",
            "Optimize resource allocation",
            "Enhance user experience design",
            "Develop mobile application features",
            "Improve data visualization tools"
    };

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("\n🚀 Starting ActiveInitializer...");

        // Check if activities already exist
        if (activeRepository.count() >= 200) {
            System.out.println("ℹ️ 200 activities already exist, skipping creation");
            return;
        }

        // Create 200 activities
        createActivities();

        // Display summary
        displayActivitySummary();

        System.out.println("\n🎉 ==========================================");
        System.out.println("✅ 200 ACTIVITIES CREATED SUCCESSFULLY!");
        System.out.println("==========================================\n");
    }

    private void createActivities() {
        List<Active> activities = new ArrayList<>();

        // Generate 200 activities with unique codes
        for (int i = 1; i <= 200; i++) {
            Active active = new Active();

            // Generate code: ACT-0001 to ACT-0200
            String code = String.format("ACT-%04d", i);
            active.setCodeActive(code);

            // Get random objective and description
            String objective = OBJECTIVES[random.nextInt(OBJECTIVES.length)];
            String description = DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)];

            // Add variety by modifying description based on code
            if (i % 10 == 0) {
                description = "[HIGH PRIORITY] " + description;
            } else if (i % 5 == 0) {
                description = "[URGENT] " + description;
            }

            active.setObjectif(objective);
            active.setDescription(description);

            activities.add(active);
        }

        int createdCount = 0;

        for (Active active : activities) {
            // Check if activity with this code already exists using Optional
            if (activeRepository.findByCodeActive(active.getCodeActive()).isPresent()) {
                System.out.println("⚠️ Activity " + active.getCodeActive() + " already exists, skipping...");
                continue;
            }

            activeRepository.save(active);
            createdCount++;

            // Print progress every 50 activities
            if (createdCount % 50 == 0) {
                System.out.println("   📝 Created " + createdCount + " activities so far...");
            }
        }

        System.out.println("\n✅ Created " + createdCount + " new activities (total: " + activeRepository.count() + ")");
    }

    private void displayActivitySummary() {
        List<Active> allActivities = activeRepository.findAll();

        System.out.println("\n📊 Activity Summary:");
        System.out.println("   Total activities: " + allActivities.size());

        // Count activities by objective categories
        long productivityCount = allActivities.stream()
                .filter(a -> a.getObjectif() != null && a.getObjectif().toLowerCase().contains("productivity"))
                .count();

        long qualityCount = allActivities.stream()
                .filter(a -> a.getObjectif() != null && a.getObjectif().toLowerCase().contains("quality"))
                .count();

        long costCount = allActivities.stream()
                .filter(a -> a.getObjectif() != null && a.getObjectif().toLowerCase().contains("cost"))
                .count();

        long customerCount = allActivities.stream()
                .filter(a -> a.getObjectif() != null && a.getObjectif().toLowerCase().contains("customer"))
                .count();

        long digitalCount = allActivities.stream()
                .filter(a -> a.getObjectif() != null && (a.getObjectif().toLowerCase().contains("digital") ||
                        a.getObjectif().toLowerCase().contains("automation")))
                .count();

        System.out.println("   By Objective Category:");
        System.out.println("      - Productivity related: " + productivityCount);
        System.out.println("      - Quality related: " + qualityCount);
        System.out.println("      - Cost related: " + costCount);
        System.out.println("      - Customer related: " + customerCount);
        System.out.println("      - Digital/Automation: " + digitalCount);

        // Sample of first 10 activities
        System.out.println("\n   Sample Activities (first 10):");
        allActivities.stream()
                .limit(10)
                .forEach(a -> System.out.println("      " + a.getCodeActive() + " | " +
                        truncateString(a.getObjectif(), 30) + " | " +
                        truncateString(a.getDescription(), 50)));
    }

    private String truncateString(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}