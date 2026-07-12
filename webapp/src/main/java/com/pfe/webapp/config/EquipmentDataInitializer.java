package com.pfe.webapp.config;

import com.pfe.webapp.entity.*;
import com.pfe.webapp.entity.materiel.*;
import com.pfe.webapp.entity.ressource.*;
import com.pfe.webapp.repository.*;
import com.pfe.webapp.repository.ressource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Order(2) // Run after MissionInitializer and EquipmentDataInitializer

public class EquipmentDataInitializer implements CommandLineRunner {

    @Autowired
    private CategorieMaterielRepository categorieMaterielRepository;

    @Autowired
    private TypeMaterielRepository typeMaterielRepository;

    @Autowired
    private MaterielRepository materielRepository;

    @Autowired
    private CategorieRessourceRepository categorieRessourceRepository;

    @Autowired
    private TypeRessourceRepository typeRessourceRepository;

    @Autowired
    private RessourceRepository ressourceRepository;

    private final Random random = ThreadLocalRandom.current();

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("🚀 Starting Equipment & Resources Data Initialization...");

        // 1️⃣ Create Equipment Categories
        createEquipmentCategories();

        // 2️⃣ Create Equipment Types
        createEquipmentTypes();

        // 3️⃣ Create Equipment Items
        createEquipment();

        // 4️⃣ Create Resource Categories
        createResourceCategories();

        // 5️⃣ Create Resource Types
        createResourceTypes();

        // 6️⃣ Create Resources
        createResources();

        System.out.println("✅ Equipment & Resources Data Initialization Complete!");
    }

    // ==============================================
    // 1️⃣ Create Equipment Categories
    // ==============================================
    private void createEquipmentCategories() {
        String[][] categories = {
                {"Seismic Acquisition Equipment"},
                {"Recording Systems"},
                {"Energy Sources"},
                {"Navigation & Positioning"},
                {"Field Support Equipment"},
                {"Safety & PPE"},
                {"Communication Systems"},
                {"Power Generation"},
                {"Vehicle Fleet"},
                {"Drilling Equipment"}
        };

        for (String[] cat : categories) {
            boolean exists = categorieMaterielRepository.findAll().stream()
                    .anyMatch(c -> c.getNom().equals(cat[0]));

            if (!exists) {
                CategorieMateriel categorie = new CategorieMateriel();
                categorie.setNom(cat[0]);
                categorieMaterielRepository.save(categorie);
                System.out.println("✅ Created category: " + cat[0]);
            } else {
                System.out.println("ℹ️ Category already exists: " + cat[0]);
            }
        }
    }

    // ==============================================
    // 2️⃣ Create Equipment Types
    // ==============================================
    private void createEquipmentTypes() {
        Object[][] types = {
                {"Geophone", "Seismic Acquisition Equipment"},
                {"Hydrophone", "Seismic Acquisition Equipment"},
                {"Seismic Cable", "Seismic Acquisition Equipment"},
                {"Geophone String", "Seismic Acquisition Equipment"},
                {"Analog Sensor", "Seismic Acquisition Equipment"},
                {"Sercel 428XL", "Recording Systems"},
                {"Sercel 508XT", "Recording Systems"},
                {"Fairfield Nodal", "Recording Systems"},
                {"Wireless Recording Unit", "Recording Systems"},
                {"Data Recorder", "Recording Systems"},
                {"Vibrator Truck", "Energy Sources"},
                {"Dynamite", "Energy Sources"},
                {"Air Gun", "Energy Sources"},
                {"Weight Drop", "Energy Sources"},
                {"Mini Vibrator", "Energy Sources"},
                {"GPS Receiver", "Navigation & Positioning"},
                {"Total Station", "Navigation & Positioning"},
                {"Digital Compass", "Navigation & Positioning"},
                {"Laser Rangefinder", "Navigation & Positioning"},
                {"RTK Base Station", "Navigation & Positioning"},
                {"Field Computer", "Field Support Equipment"},
                {"External Hard Drive", "Field Support Equipment"},
                {"Printer", "Field Support Equipment"},
                {"Battery Pack", "Field Support Equipment"},
                {"Solar Panel", "Field Support Equipment"},
                {"Hard Hat", "Safety & PPE"},
                {"Safety Vest", "Safety & PPE"},
                {"Safety Boots", "Safety & PPE"},
                {"First Aid Kit", "Safety & PPE"},
                {"Gas Detector", "Safety & PPE"},
                {"Motorola Radio", "Communication Systems"},
                {"Satellite Phone", "Communication Systems"},
                {"VHF Radio", "Communication Systems"},
                {"Walkie Talkie", "Communication Systems"},
                {"Generator 5kW", "Power Generation"},
                {"Generator 10kW", "Power Generation"},
                {"Inverter", "Power Generation"},
                {"Power Distributor", "Power Generation"},
                {"Toyota Hilux", "Vehicle Fleet"},
                {"Nissan Patrol", "Vehicle Fleet"},
                {"Water Truck", "Vehicle Fleet"},
                {"Crew Bus", "Vehicle Fleet"},
                {"Portable Drill", "Drilling Equipment"},
                {"Drill Bit", "Drilling Equipment"},
                {"Auger Drill", "Drilling Equipment"},
                {"Drill Rod", "Drilling Equipment"}
        };

        for (Object[] type : types) {
            String typeName = (String) type[0];
            String categoryName = (String) type[1];

            boolean exists = typeMaterielRepository.findAll().stream()
                    .anyMatch(t -> t.getLibelle().equals(typeName));

            if (!exists) {
                CategorieMateriel categorie = categorieMaterielRepository.findAll().stream()
                        .filter(c -> c.getNom().equals(categoryName))
                        .findFirst()
                        .orElse(null);

                if (categorie != null) {
                    TypeMateriel typeMateriel = new TypeMateriel();
                    typeMateriel.setLibelle(typeName);
                    typeMateriel.setCategorie(categorie);
                    typeMaterielRepository.save(typeMateriel);
                    System.out.println("✅ Created type: " + typeName);
                }
            } else {
                System.out.println("ℹ️ Type already exists: " + typeName);
            }
        }
    }

    // ==============================================
    // 3️⃣ Create Equipment Items
    // ==============================================
    private void createEquipment() {
        List<TypeMateriel> allTypes = typeMaterielRepository.findAll();

        for (TypeMateriel type : allTypes) {
            if (!materielRepository.findByTypeMaterielIdTypeMateriel(type.getIdTypeMateriel()).isEmpty()) {
                continue;
            }

            int quantity = getQuantityForType(type.getLibelle());

            for (int i = 1; i <= quantity; i++) {
                Materiel materiel = new Materiel();
                materiel.setCodeMateriel(generateCode(type.getLibelle(), i));
                materiel.setMarque(getRandomBrand(type.getLibelle()));
                materiel.setModele(getRandomModel());
                materiel.setDesignation(type.getLibelle() + " - " + materiel.getMarque() + " " + materiel.getModele());
                materiel.setDateAchat(generateRandomPurchaseDate());
                materiel.setPrix(generateRandomPrice(type.getLibelle()));
                materiel.setStatus(getRandomStatus());
                materiel.setTypeMateriel(type);

                materielRepository.save(materiel);
            }
            System.out.println("✅ Created " + quantity + " units for type: " + type.getLibelle());
        }
    }

    // ==============================================
    // 4️⃣ Create Resource Categories
    // ==============================================
    private void createResourceCategories() {
        String[] categories = {
                "Explosives & Ammunition",
                "Fuel & Lubricants",
                "Field Supplies",
                "Spare Parts",
                "Office Supplies",
                "Safety Materials",
                "Drilling Consumables",
                "IT Equipment",
                "Medical Supplies",
                "Water & Food"
        };

        for (String cat : categories) {
            boolean exists = categorieRessourceRepository.findAll().stream()
                    .anyMatch(c -> c.getNom().equals(cat));

            if (!exists) {
                CategorieRessource categorie = new CategorieRessource();
                categorie.setNom(cat);
                categorieRessourceRepository.save(categorie);
                System.out.println("✅ Created resource category: " + cat);
            } else {
                System.out.println("ℹ️ Resource category already exists: " + cat);
            }
        }
    }

    // ==============================================
    // 5️⃣ Create Resource Types
    // ==============================================
    private void createResourceTypes() {
        Object[][] types = {
                {"Dynamite", "Explosives & Ammunition"},
                {"Detonator", "Explosives & Ammunition"},
                {"Primer", "Explosives & Ammunition"},
                {"Safety Fuse", "Explosives & Ammunition"},
                {"Diesel Fuel", "Fuel & Lubricants"},
                {"Gasoline", "Fuel & Lubricants"},
                {"Engine Oil", "Fuel & Lubricants"},
                {"Hydraulic Oil", "Fuel & Lubricants"},
                {"Grease", "Fuel & Lubricants"},
                {"Bottled Water", "Field Supplies"},
                {"MRE Meals", "Field Supplies"},
                {"Camping Tent", "Field Supplies"},
                {"Sleeping Bag", "Field Supplies"},
                {"Tire", "Spare Parts"},
                {"Battery", "Spare Parts"},
                {"Filter", "Spare Parts"},
                {"Spark Plug", "Spare Parts"},
                {"Brake Pad", "Spare Parts"},
                {"A4 Paper", "Office Supplies"},
                {"Pen", "Office Supplies"},
                {"Notebook", "Office Supplies"},
                {"Printer Ink", "Office Supplies"},
                {"Safety Glasses", "Safety Materials"},
                {"Gloves", "Safety Materials"},
                {"Ear Plugs", "Safety Materials"},
                {"Fire Extinguisher", "Safety Materials"},
                {"Drill Bit", "Drilling Consumables"},
                {"Mud", "Drilling Consumables"},
                {"Casing Pipe", "Drilling Consumables"},
                {"USB Drive", "IT Equipment"},
                {"External HDD", "IT Equipment"},
                {"Laptop", "IT Equipment"},
                {"First Aid Kit", "Medical Supplies"},
                {"Pain Killer", "Medical Supplies"},
                {"Bandage", "Medical Supplies"},
                {"Antiseptic", "Medical Supplies"},
                {"Water Tank", "Water & Food"},
                {"Canned Food", "Water & Food"},
                {"Energy Bars", "Water & Food"}
        };

        for (Object[] type : types) {
            String typeName = (String) type[0];
            String categoryName = (String) type[1];

            boolean exists = typeRessourceRepository.findAll().stream()
                    .anyMatch(t -> t.getNom().equals(typeName));

            if (!exists) {
                CategorieRessource categorie = categorieRessourceRepository.findAll().stream()
                        .filter(c -> c.getNom().equals(categoryName))
                        .findFirst()
                        .orElse(null);

                if (categorie != null) {
                    TypeRessource typeRessource = new TypeRessource();
                    typeRessource.setNom(typeName);
                    typeRessource.setCategorieRessource(categorie);
                    typeRessourceRepository.save(typeRessource);
                    System.out.println("✅ Created resource type: " + typeName);
                }
            } else {
                System.out.println("ℹ️ Resource type already exists: " + typeName);
            }
        }
    }

    // ==============================================
    // 6️⃣ Create Resources - FIXED with unite and description
    // ==============================================
    private void createResources() {
        List<TypeRessource> allTypes = typeRessourceRepository.findAll();

        for (TypeRessource type : allTypes) {
            if (!ressourceRepository.findByTypeRessourceIdTypeRessource(type.getIdTypeRessource()).isEmpty()) {
                continue;
            }

            int quantity = getResourceQuantityForType(type.getNom());

            for (int i = 1; i <= quantity; i++) {
                Ressource ressource = new Ressource();
                ressource.setTitre(type.getNom() + " - Batch " + i);
                ressource.setTypeRessource(type);
                ressource.setQuantite(generateRandomQuantity());
                ressource.setDateAchat(generateRandomPurchaseDate());
                ressource.setCout(getCostForResourceType(type.getNom()));
                ressource.setUnite(getUnitForResourceType(type.getNom()));        // ✅ Added
                ressource.setDescription("Batch of " + type.getNom() + " for seismic operations");  // ✅ Added

                ressourceRepository.save(ressource);
            }
            System.out.println("✅ Created " + quantity + " batches for resource: " + type.getNom());
        }
    }

    // ==============================================
    // Helper Methods for Resources
    // ==============================================

    private String getUnitForResourceType(String typeNom) {
        if (typeNom.contains("Dynamite")) return "Case";
        if (typeNom.contains("Detonator") || typeNom.contains("Primer")) return "Box";
        if (typeNom.contains("Safety Fuse")) return "Roll";
        if (typeNom.contains("Diesel") || typeNom.contains("Gasoline")) return "Liter";
        if (typeNom.contains("Oil")) return "Liter";
        if (typeNom.contains("Grease")) return "KG";
        if (typeNom.contains("Bottled Water")) return "Box";
        if (typeNom.contains("MRE")) return "Box";
        if (typeNom.contains("Camping Tent") || typeNom.contains("Sleeping Bag")) return "Unit";
        if (typeNom.contains("Tire") || typeNom.contains("Battery")) return "Unit";
        if (typeNom.contains("Filter") || typeNom.contains("Spark Plug")) return "Box";
        if (typeNom.contains("Brake Pad")) return "Set";
        if (typeNom.contains("A4 Paper")) return "Ream";
        if (typeNom.contains("Pen")) return "Box";
        if (typeNom.contains("Notebook")) return "Unit";
        if (typeNom.contains("Printer Ink")) return "Cartridge";
        if (typeNom.contains("Safety Glasses") || typeNom.contains("Gloves")) return "Pair";
        if (typeNom.contains("Ear Plugs")) return "Box";
        if (typeNom.contains("Fire Extinguisher")) return "Unit";
        if (typeNom.contains("Drill Bit")) return "Unit";
        if (typeNom.contains("Mud")) return "Bag";
        if (typeNom.contains("Casing Pipe")) return "Meter";
        if (typeNom.contains("USB Drive") || typeNom.contains("External HDD") || typeNom.contains("Laptop")) return "Unit";
        if (typeNom.contains("First Aid Kit")) return "Kit";
        if (typeNom.contains("Pain Killer") || typeNom.contains("Bandage")) return "Box";
        if (typeNom.contains("Antiseptic")) return "Bottle";
        if (typeNom.contains("Water Tank") || typeNom.contains("Canned Food")) return "Unit";
        if (typeNom.contains("Energy Bars")) return "Box";
        return "Unit";
    }

    private double getCostForResourceType(String typeNom) {
        if (typeNom.contains("Dynamite")) return 250.0;
        if (typeNom.contains("Detonator")) return 50.0;
        if (typeNom.contains("Primer")) return 75.0;
        if (typeNom.contains("Safety Fuse")) return 30.0;
        if (typeNom.contains("Diesel") || typeNom.contains("Gasoline")) return 1.2;
        if (typeNom.contains("Engine Oil") || typeNom.contains("Hydraulic Oil")) return 5.0;
        if (typeNom.contains("Grease")) return 8.0;
        if (typeNom.contains("Bottled Water")) return 3.0;
        if (typeNom.contains("MRE")) return 15.0;
        if (typeNom.contains("Camping Tent")) return 120.0;
        if (typeNom.contains("Sleeping Bag")) return 45.0;
        if (typeNom.contains("Tire")) return 80.0;
        if (typeNom.contains("Battery")) return 60.0;
        if (typeNom.contains("Filter")) return 15.0;
        if (typeNom.contains("Spark Plug")) return 5.0;
        if (typeNom.contains("Brake Pad")) return 35.0;
        if (typeNom.contains("A4 Paper")) return 2.5;
        if (typeNom.contains("Pen")) return 5.0;
        if (typeNom.contains("Notebook")) return 2.0;
        if (typeNom.contains("Printer Ink")) return 25.0;
        if (typeNom.contains("Safety Glasses")) return 8.0;
        if (typeNom.contains("Gloves")) return 5.0;
        if (typeNom.contains("Ear Plugs")) return 10.0;
        if (typeNom.contains("Fire Extinguisher")) return 45.0;
        if (typeNom.contains("Drill Bit")) return 120.0;
        if (typeNom.contains("Mud")) return 25.0;
        if (typeNom.contains("Casing Pipe")) return 15.0;
        if (typeNom.contains("USB Drive")) return 20.0;
        if (typeNom.contains("External HDD")) return 80.0;
        if (typeNom.contains("Laptop")) return 800.0;
        if (typeNom.contains("First Aid Kit")) return 30.0;
        if (typeNom.contains("Pain Killer")) return 10.0;
        if (typeNom.contains("Bandage")) return 8.0;
        if (typeNom.contains("Antiseptic")) return 5.0;
        if (typeNom.contains("Water Tank")) return 150.0;
        if (typeNom.contains("Canned Food")) return 40.0;
        if (typeNom.contains("Energy Bars")) return 20.0;
        return 10.0;
    }

    // ==============================================
    // Helper Methods
    // ==============================================

    private int getQuantityForType(String typeLibelle) {
        if (typeLibelle.contains("Geophone") || typeLibelle.contains("Hydrophone")) return 300;
        if (typeLibelle.contains("Cable")) return 100;
        if (typeLibelle.contains("GPS") || typeLibelle.contains("Radio")) return 50;
        if (typeLibelle.contains("Hard Hat") || typeLibelle.contains("Safety Vest")) return 200;
        if (typeLibelle.contains("Battery")) return 150;
        if (typeLibelle.contains("Computer") || typeLibelle.contains("Printer")) return 30;
        if (typeLibelle.contains("Generator")) return 20;
        if (typeLibelle.contains("Vehicle") || typeLibelle.contains("Truck")) return 25;
        if (typeLibelle.contains("Drill")) return 50;
        return 20;
    }

    private int getResourceQuantityForType(String typeNom) {
        if (typeNom.contains("Dynamite") || typeNom.contains("Detonator")) return 100;
        if (typeNom.contains("Fuel") || typeNom.contains("Oil")) return 50;
        if (typeNom.contains("Water") || typeNom.contains("MRE")) return 30;
        if (typeNom.contains("Tire") || typeNom.contains("Battery")) return 40;
        if (typeNom.contains("Paper") || typeNom.contains("Pen")) return 50;
        if (typeNom.contains("Safety Glasses") || typeNom.contains("Gloves")) return 60;
        if (typeNom.contains("First Aid")) return 25;
        return 20;
    }

    private String generateCode(String type, int index) {
        String prefix = type.substring(0, Math.min(3, type.length())).toUpperCase();
        return prefix + "-" + String.format("%04d", index);
    }

    private String getRandomBrand(String type) {
        if (type.contains("Sercel")) return "Sercel";
        if (type.contains("Fairfield")) return "Fairfield";
        if (type.contains("Toyota")) return "Toyota";
        if (type.contains("Nissan")) return "Nissan";
        if (type.contains("Motorola")) return "Motorola";

        String[] brands = {"Samsung", "Honeywell", "Bosch", "Siemens", "GE", "Schneider",
                "Caterpillar", "Komatsu", "Garmin", "Leica", "Trimble"};
        return brands[random.nextInt(brands.length)];
    }

    private String getRandomModel() {
        return "M-" + (random.nextInt(999) + 100);
    }

    private LocalDate generateRandomPurchaseDate() {
        return LocalDate.of(
                random.nextInt(2018, 2025),
                random.nextInt(1, 13),
                random.nextInt(1, 29)
        );
    }

    private Double generateRandomPrice(String type) {
        if (type.contains("Geophone") || type.contains("Hydrophone")) return 150.0 + random.nextDouble() * 100;
        if (type.contains("Recording") || type.contains("System")) return 50000.0 + random.nextDouble() * 30000;
        if (type.contains("Vibrator")) return 250000.0 + random.nextDouble() * 100000;
        if (type.contains("GPS") || type.contains("Radio")) return 800.0 + random.nextDouble() * 500;
        if (type.contains("Generator")) return 3000.0 + random.nextDouble() * 2000;
        if (type.contains("Vehicle") || type.contains("Truck")) return 35000.0 + random.nextDouble() * 15000;
        if (type.contains("Drill")) return 2000.0 + random.nextDouble() * 1000;
        return 500.0 + random.nextDouble() * 1000;
    }

    private StatusMateriel getRandomStatus() {
        double r = random.nextDouble();
        if (r < 0.7) return StatusMateriel.EN_BON_ETAT;
        if (r < 0.85) return StatusMateriel.EN_PANNE;
        if (r < 0.95) return StatusMateriel.EN_REPARATION_INTERNE;
        return StatusMateriel.EN_REPARATION_EXTERNE;
    }

    private double generateRandomQuantity() {
        return 50 + random.nextInt(150);
    }
}