package com.pfe.webapp.config;

import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.*;

import com.pfe.webapp.repository.medical.EtatMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Order(1) // Add this - Run FIRST

public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AffectationRoleRepository affectationRoleRepository;

    @Autowired
    private EtatMedicalRepository etatMedicalRepository;

    @Autowired
    private FonctionRepository fonctionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Random random = ThreadLocalRandom.current();

    // SONATRACH Job Functions (Oil & Gas Company)
    private final FonctionData[] FONCTIONS = {
            // Executive & Management
            new FonctionData("Directeur Général", "Overall company strategy and leadership"),
            new FonctionData("Directeur des Opérations", "Manages daily operations and production"),
            new FonctionData("Directeur Technique", "Oversees technical departments and engineering"),
            new FonctionData("Directeur des Ressources Humaines", "Manages HR, recruitment, and employee relations"),
            new FonctionData("Directeur Financier", "Manages finance, budgeting, and accounting"),
            new FonctionData("Directeur HSE", "Health, Safety, and Environment management"),

            // Engineering & Technical
            new FonctionData("Ingénieur Pétrolier", "Petroleum engineering and reservoir management"),
            new FonctionData("Ingénieur Gazier", "Natural gas engineering and processing"),
            new FonctionData("Ingénieur Raffinage", "Refinery operations and optimization"),
            new FonctionData("Ingénieur Chimiste", "Chemical process engineering"),
            new FonctionData("Ingénieur Mécanique", "Mechanical engineering and maintenance"),
            new FonctionData("Ingénieur Électrique", "Electrical systems and power management"),
            new FonctionData("Ingénieur Civil", "Civil engineering for infrastructure"),
            new FonctionData("Ingénieur Instrumentation", "Control systems and instrumentation"),
            new FonctionData("Ingénieur Automatisation", "Automation and SCADA systems"),
            new FonctionData("Ingénieur Sécurité Industrielle", "Industrial safety engineering"),

            // Field Operations
            new FonctionData("Chef de Site", "Site operations manager"),
            new FonctionData("Chef de Quart", "Shift supervisor"),
            new FonctionData("Opérateur de Production", "Production operations"),
            new FonctionData("Opérateur de Salle de Contrôle", "Control room operator"),
            new FonctionData("Technicien de Maintenance", "Maintenance technician"),
            new FonctionData("Technicien Électrique", "Electrical technician"),
            new FonctionData("Technicien Mécanique", "Mechanical technician"),
            new FonctionData("Technicien Instrumentation", "Instrumentation technician"),
            new FonctionData("Soudeur", "Welding specialist"),
            new FonctionData("Electricien", "Electrical worker"),
            new FonctionData("Plombier Industriel", "Industrial plumber"),

            // HSE & Quality
            new FonctionData("Responsable HSE", "HSE department manager"),
            new FonctionData("Ingénieur HSE", "Health, Safety, and Environment engineer"),
            new FonctionData("Inspecteur HSE", "Safety inspector"),
            new FonctionData("Responsable Qualité", "Quality management"),
            new FonctionData("Contrôleur Qualité", "Quality control inspector"),

            // Laboratory & Research
            new FonctionData("Chef de Laboratoire", "Laboratory manager"),
            new FonctionData("Chimiste Analyste", "Analytical chemist"),
            new FonctionData("Technicien de Laboratoire", "Laboratory technician"),

            // Logistics & Supply Chain
            new FonctionData("Responsable Logistique", "Logistics manager"),
            new FonctionData("Responsable Approvisionnement", "Procurement manager"),
            new FonctionData("Coordinateur Transport", "Transport coordinator"),
            new FonctionData("Magasinier", "Storekeeper"),

            // Administrative & Support
            new FonctionData("Responsable Administratif", "Administrative manager"),
            new FonctionData("Comptable", "Accountant"),
            new FonctionData("Assistant RH", "HR assistant"),
            new FonctionData("Secrétaire", "Secretary"),
            new FonctionData("Assistant Administratif", "Administrative assistant"),

            // IT & Digital
            new FonctionData("Responsable IT", "IT department manager"),
            new FonctionData("Ingénieur Systèmes", "Systems engineer"),
            new FonctionData("Développeur", "Software developer"),
            new FonctionData("Technicien IT", "IT technician"),

            // Maintenance & Services
            new FonctionData("Responsable Maintenance", "Maintenance manager"),
            new FonctionData("Superviseur Maintenance", "Maintenance supervisor"),
            new FonctionData("Mécanicien", "Mechanic"),
            new FonctionData("Chauffeur", "Driver"),
            new FonctionData("Agent de Sécurité", "Security guard"),
            new FonctionData("Agent d'Entretien", "Cleaner")
    };

    private final String[] FIRST_NAMES = {
            "Ahmed", "Mohamed", "Karim", "Nadia", "Sofia", "Youssef", "Imane", "Omar", "Fatima", "Ali",
            "Hassan", "Leila", "Sami", "Rania", "Amine", "Salma", "Mehdi", "Houda", "Reda", "Asma",
            "Bilal", "Meryem", "Rachid", "Yasmine", "Sofiane", "Nabila", "Hamza", "Latifa", "Imad", "Nour"
    };

    private final String[] LAST_NAMES = {
            "Benali", "Said", "Lounis", "Khelifa", "Boukadoum", "Mansouri", "Boudiaf", "Haddad", "Saadi", "Ferhat",
            "Belkacem", "Taleb", "Meziane", "Zerrouki", "Hamidi", "Cherif", "Dahmani", "Hakim", "Mokhtar", "Nadir"
    };

    private final String[] BLOOD_TYPES = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};

    @Override
    @Transactional
    public void run(String... args) {

        System.out.println("🚀 Starting DataInitializer for SONATRACH ERP...");

        // 1️⃣ Create all job functions
        List<Fonction> allFonctions = createAllFonctions();

        // 2️⃣ Create roles
        Role adminRole = createRoleIfNotExists("ADMIN", TypeRole.ADMINISTRATEUR);
        Role chefMissionRole = createRoleIfNotExists("CHEF_MISSION", TypeRole.CHEF_MISSION);
        Role chefTerrainRole = createRoleIfNotExists("CHEF_TERRAIN", TypeRole.CHEF_TERRAIN);
        Role directeurRole = createRoleIfNotExists("DIRECTEUR", TypeRole.DIRECTEUR);
        Role gestionnaireRole = createRoleIfNotExists("GESTIONNAIRE", TypeRole.Gestionnaire);

        System.out.println("✅ Roles created/verified");

        // 3️⃣ Create accounts with functions
        createAdminAccount(adminRole, findFonctionByName(allFonctions, "Directeur Général"));
        createDirecteurs(directeurRole, 3, allFonctions);
        createChefMission(chefMissionRole, 13, allFonctions);
        createChefTerrain(chefTerrainRole, 13, allFonctions);
        createGestionnaires(gestionnaireRole, 30, allFonctions);

        // 4️⃣ Create 500 employees with functions (no accounts)
        createEmployesWithoutAccounts(500, allFonctions);

        System.out.println("\n🎉 ==========================================");
        System.out.println("✅ ALL USERS CREATED SUCCESSFULLY!");
        System.out.println("   - 1 ADMIN");
        System.out.println("   - 3 DIRECTEUR");
        System.out.println("   - 13 CHEF_MISSION");
        System.out.println("   - 13 CHEF_TERRAIN");
        System.out.println("   - 30 GESTIONNAIRE");
        System.out.println("   - 500 EMPLOYES (with job functions)");
        System.out.println("   - " + allFonctions.size() + " JOB FUNCTIONS");
        System.out.println("==========================================\n");
    }

    // ==============================================
    // Create all job functions
    // ==============================================
    private List<Fonction> createAllFonctions() {
        List<Fonction> fonctions = new ArrayList<>();

        for (FonctionData fd : FONCTIONS) {
            Fonction fonction = fonctionRepository.findByNom(fd.nom)
                    .orElseGet(() -> {
                        Fonction newFonction = new Fonction();
                        newFonction.setNom(fd.nom);
                        newFonction.setDescription(fd.description);
                        return fonctionRepository.save(newFonction);
                    });
            fonctions.add(fonction);
            System.out.println("   ✅ Fonction: " + fd.nom);
        }

        System.out.println("✅ Created " + fonctions.size() + " job functions");
        return fonctions;
    }

    private Fonction findFonctionByName(List<Fonction> fonctions, String name) {
        return fonctions.stream()
                .filter(f -> f.getNom().equals(name))
                .findFirst()
                .orElse(fonctions.get(0));
    }

    // ==============================================
    // Create Admin account
    // ==============================================
    private void createAdminAccount(Role adminRole, Fonction fonction) {
        if (compteRepository.findByUsername("admin").isEmpty()) {
            Employe adminEmploye = createEmploye(
                    "Admin", "System", "admin@sonatrach.dz",
                    getRandomAddress(), getRandomBirthPlace(), fonction
            );

            EtatMedical etatMedical = createEtatMedical(adminEmploye, "O+");
            etatMedicalRepository.save(etatMedical);

            Compte adminCompte = createCompte("admin", "admin123", adminEmploye);
            compteRepository.save(adminCompte);

            adminEmploye.setCompte(adminCompte);
            employeRepository.save(adminEmploye);

            assignRoleToAccount(adminCompte, adminRole);

            System.out.println("✅ Admin account created with fonction: " + fonction.getNom());
        } else {
            System.out.println("ℹ️ Admin user already exists");
        }
    }

    // ==============================================
    // Create DIRECTEUR
    // ==============================================
    private void createDirecteurs(Role role, int targetCount, List<Fonction> allFonctions) {
        int currentCount = countExistingUsersByUsernamePrefix("directeur");

        if (currentCount >= targetCount) {
            System.out.println("ℹ️ " + targetCount + " DIRECTEUR already exist");
            return;
        }

        int toCreate = targetCount - currentCount;
        Fonction[] directeurFonctions = {
                findFonctionByName(allFonctions, "Directeur Général"),
                findFonctionByName(allFonctions, "Directeur des Opérations"),
                findFonctionByName(allFonctions, "Directeur Technique")
        };

        for (int i = currentCount + 1; i <= currentCount + toCreate; i++) {
            String username = "directeur" + i;

            if (compteRepository.findByUsername(username).isPresent()) {
                System.out.println("⚠️ User " + username + " already exists, skipping...");
                continue;
            }

            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = "directeur." + i + "@sonatrach.dz";

            Fonction fonction = directeurFonctions[(i - 1) % directeurFonctions.length];

            createUserWithAccount(firstName, lastName, email, username, role, fonction);
        }
        System.out.println("✅ Created " + toCreate + " new DIRECTEUR");
    }

    // ==============================================
    // Create CHEF_MISSION
    // ==============================================
    private void createChefMission(Role role, int targetCount, List<Fonction> allFonctions) {
        int currentCount = countExistingUsersByUsernamePrefix("chefmission");

        if (currentCount >= targetCount) {
            System.out.println("ℹ️ " + targetCount + " CHEF_MISSION already exist");
            return;
        }

        int toCreate = targetCount - currentCount;
        Fonction[] chefMissionFonctions = {
                findFonctionByName(allFonctions, "Chef de Site"),
                findFonctionByName(allFonctions, "Chef de Quart"),
                findFonctionByName(allFonctions, "Responsable HSE"),
                findFonctionByName(allFonctions, "Responsable Maintenance"),
                findFonctionByName(allFonctions, "Responsable Logistique"),
                findFonctionByName(allFonctions, "Responsable Qualité"),
                findFonctionByName(allFonctions, "Chef de Laboratoire")
        };

        for (int i = currentCount + 1; i <= currentCount + toCreate; i++) {
            String username = "chefmission" + i;

            if (compteRepository.findByUsername(username).isPresent()) {
                System.out.println("⚠️ User " + username + " already exists, skipping...");
                continue;
            }

            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = "chef.mission." + i + "@sonatrach.dz";

            Fonction fonction = chefMissionFonctions[random.nextInt(chefMissionFonctions.length)];

            createUserWithAccount(firstName, lastName, email, username, role, fonction);
        }
        System.out.println("✅ Created " + toCreate + " new CHEF_MISSION");
    }

    // ==============================================
    // Create CHEF_TERRAIN
    // ==============================================
    private void createChefTerrain(Role role, int targetCount, List<Fonction> allFonctions) {
        int currentCount = countExistingUsersByUsernamePrefix("chefterrain");

        if (currentCount >= targetCount) {
            System.out.println("ℹ️ " + targetCount + " CHEF_TERRAIN already exist");
            return;
        }

        int toCreate = targetCount - currentCount;
        Fonction[] chefTerrainFonctions = {
                findFonctionByName(allFonctions, "Superviseur Maintenance"),
                findFonctionByName(allFonctions, "Ingénieur HSE"),
                findFonctionByName(allFonctions, "Coordinateur Transport"),
                findFonctionByName(allFonctions, "Contrôleur Qualité")
        };

        for (int i = currentCount + 1; i <= currentCount + toCreate; i++) {
            String username = "chefterrain" + i;

            if (compteRepository.findByUsername(username).isPresent()) {
                System.out.println("⚠️ User " + username + " already exists, skipping...");
                continue;
            }

            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = "chef.terrain." + i + "@sonatrach.dz";

            Fonction fonction = chefTerrainFonctions[random.nextInt(chefTerrainFonctions.length)];

            createUserWithAccount(firstName, lastName, email, username, role, fonction);
        }
        System.out.println("✅ Created " + toCreate + " new CHEF_TERRAIN");
    }

    // ==============================================
    // Create GESTIONNAIRE
    // ==============================================
    private void createGestionnaires(Role role, int targetCount, List<Fonction> allFonctions) {
        int currentCount = countExistingUsersByUsernamePrefix("gestionnaire");

        if (currentCount >= targetCount) {
            System.out.println("ℹ️ " + targetCount + " GESTIONNAIRE already exist");
            return;
        }

        int toCreate = targetCount - currentCount;
        Fonction[] gestionnaireFonctions = {
                findFonctionByName(allFonctions, "Comptable"),
                findFonctionByName(allFonctions, "Assistant RH"),
                findFonctionByName(allFonctions, "Assistant Administratif"),
                findFonctionByName(allFonctions, "Responsable Administratif"),
                findFonctionByName(allFonctions, "Directeur des Ressources Humaines"),
                findFonctionByName(allFonctions, "Directeur Financier")
        };

        for (int i = currentCount + 1; i <= currentCount + toCreate; i++) {
            String username = "gestionnaire" + i;

            if (compteRepository.findByUsername(username).isPresent()) {
                System.out.println("⚠️ User " + username + " already exists, skipping...");
                continue;
            }

            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = "gestionnaire." + i + "@sonatrach.dz";

            Fonction fonction = gestionnaireFonctions[random.nextInt(gestionnaireFonctions.length)];

            createUserWithAccount(firstName, lastName, email, username, role, fonction);
        }
        System.out.println("✅ Created " + toCreate + " new GESTIONNAIRE");
    }

    // ==============================================
    // Create 500 employees without accounts (with functions)
    // ==============================================
    private void createEmployesWithoutAccounts(int targetCount, List<Fonction> allFonctions) {
        List<Employe> existingEmployesWithoutAccounts = employeRepository.findAll().stream()
                .filter(e -> e.getCompte() == null)
                .toList();

        int currentCount = existingEmployesWithoutAccounts.size();

        if (currentCount >= targetCount) {
            System.out.println("ℹ️ " + targetCount + " employes sans comptes existent déjà");
            return;
        }

        int toCreate = targetCount - currentCount;

        for (int i = 0; i < toCreate; i++) {
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String email = "employe." + (currentCount + i + 1) + "@sonatrach.dz";

            // Randomly assign a function
            Fonction fonction = allFonctions.get(random.nextInt(allFonctions.size()));

            // Create employe WITHOUT compte
            Employe employe = createEmploye(
                    firstName,
                    lastName,
                    email,
                    getRandomAddress(),
                    getRandomBirthPlace(),
                    fonction
            );

            // Create medical file
            EtatMedical etatMedical = createEtatMedical(employe, getRandomBloodType());
            etatMedicalRepository.save(etatMedical);

            System.out.println("   ✅ Created employe: " + firstName + " " + lastName + " - " + fonction.getNom());
        }

        System.out.println("✅ Created " + toCreate + " new employes sans comptes");
    }

    // ==============================================
    // Create user with account
    // ==============================================
    private void createUserWithAccount(String firstName, String lastName, String email, String username,
                                       Role role, Fonction fonction) {

        if (compteRepository.findByUsername(username).isPresent()) {
            System.out.println("⚠️ User " + username + " already exists, skipping creation");
            return;
        }

        // Create employee
        Employe employe = createEmploye(firstName, lastName, email, getRandomAddress(), getRandomBirthPlace(), fonction);

        // Create medical file
        EtatMedical etatMedical = createEtatMedical(employe, getRandomBloodType());
        etatMedicalRepository.save(etatMedical);

        // Create account
        Compte compte = createCompte(username, "password123", employe);
        compteRepository.save(compte);

        // Link account to employee
        employe.setCompte(compte);
        employeRepository.save(employe);

        // Assign role to account
        assignRoleToAccount(compte, role);

        System.out.println("   ✅ Created with account: " + username + " (" + role.getName() + ") - " + fonction.getNom());
    }

    // ==============================================
    // Helper Methods
    // ==============================================

    private Employe createEmploye(String firstName, String lastName, String email,
                                  String address, String birthPlace, Fonction fonction) {
        Employe employe = new Employe();
        employe.setNom(lastName);
        employe.setPrenom(firstName);
        employe.setDateNaissance(generateRandomBirthDate());
        employe.setEmail(email);
        employe.setNumTel(generateRandomPhoneNumber());
        employe.setAdresse(address);
        employe.setLieuNaissance(birthPlace);
        employe.setSexe(random.nextBoolean() ? SexeType.HOMME : SexeType.FEMME);
        employe.setNumIdentite(generateRandomIdNumber());
        employe.setFonction(fonction);

        return employeRepository.save(employe);
    }

    private EtatMedical createEtatMedical(Employe employe, String bloodType) {
        EtatMedical etatMedical = new EtatMedical();
        etatMedical.setGroupeSanguin(bloodType);
        etatMedical.setAllergies(generateRandomAllergies());
        etatMedical.setVaccinations("COVID-19, Flu, Hepatitis B");
        etatMedical.setMedicationsActuelles(generateRandomMedications());
        etatMedical.setMedecinTraitant(generateRandomDoctorName());
        etatMedical.setDerniereVisiteMedicale(generateRandomDate());
        etatMedical.setEmploye(employe);
        return etatMedical;
    }

    private Compte createCompte(String username, String password, Employe employe) {
        Compte compte = new Compte();
        compte.setUsername(username);
        compte.setPassword(passwordEncoder.encode(password));
        compte.setStatus(StatusCompte.ACTIVE);
        compte.setEmploye(employe);
        return compte;
    }

    private void assignRoleToAccount(Compte compte, Role role) {
        AffectationRole affectation = new AffectationRole();
        affectation.setCompte(compte);
        affectation.setRole(role);
        affectation.setDateDebut(LocalDate.now());
        affectation.setDateFin(null);
        affectation.setActive(true);
        affectationRoleRepository.save(affectation);
    }

    private Role createRoleIfNotExists(String name, TypeRole type) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(name);
                    newRole.setType(type);
                    return roleRepository.save(newRole);
                });
    }

    private int countExistingUsersByUsernamePrefix(String prefix) {
        List<Compte> allComptes = compteRepository.findAll();
        return (int) allComptes.stream()
                .filter(c -> c.getUsername().startsWith(prefix))
                .count();
    }

    // ==============================================
    // Random Data Generators
    // ==============================================

    private LocalDate generateRandomBirthDate() {
        return LocalDate.of(
                random.nextInt(1970, 2000),
                random.nextInt(1, 13),
                random.nextInt(1, 29)
        );
    }

    private String generateRandomPhoneNumber() {
        return "+213" + String.format("%09d", random.nextInt(100000000));
    }

    private String generateRandomIdNumber() {
        return String.format("%010d", random.nextInt(100000000));
    }

    private String getRandomAddress() {
        String[] addresses = {
                "123 Rue Didouche Mourad, Alger", "45 Boulevard Krim Belkacem, Oran",
                "78 Rue Larbi Ben M'hidi, Constantine", "12 Avenue de l'ALN, Annaba",
                "34 Rue des Frères Bouadou, Blida", "56 Boulevard Zabana, Sétif",
                "Cité SONATRACH, Hassi Messaoud", "Complexe Pétrochimique, Skikda",
                "Zone Industrielle, Arzew", "Base de Vie, In Amenas"
        };
        return addresses[random.nextInt(addresses.length)];
    }

    private String getRandomBirthPlace() {
        String[] places = {"Alger", "Oran", "Constantine", "Annaba", "Blida", "Sétif",
                "Tizi Ouzou", "Béjaïa", "Hassi Messaoud", "Skikda", "Arzew"};
        return places[random.nextInt(places.length)];
    }

    private String getRandomBloodType() {
        return BLOOD_TYPES[random.nextInt(BLOOD_TYPES.length)];
    }

    private String generateRandomAllergies() {
        String[] allergies = {"None", "Pollen", "Dust", "Penicillin", "None"};
        return allergies[random.nextInt(allergies.length)];
    }

    private String generateRandomMedications() {
        String[] medications = {"None", "Paracetamol", "Ibuprofen", "Vitamins"};
        return medications[random.nextInt(medications.length)];
    }

    private String generateRandomDoctorName() {
        String[] doctors = {"Dr. Benali", "Dr. Said", "Dr. Lounis", "Dr. Khelifa"};
        return doctors[random.nextInt(doctors.length)];
    }

    private LocalDate generateRandomDate() {
        return LocalDate.of(
                random.nextInt(2020, 2025),
                random.nextInt(1, 13),
                random.nextInt(1, 29)
        );
    }

    // ==============================================
    // Inner class for function data
    // ==============================================
    private static class FonctionData {
        String nom;
        String description;

        FonctionData(String nom, String description) {
            this.nom = nom;
            this.description = description;
        }
    }
}