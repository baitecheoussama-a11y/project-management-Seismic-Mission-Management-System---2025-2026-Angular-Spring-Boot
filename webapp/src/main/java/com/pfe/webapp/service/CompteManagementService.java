package com.pfe.webapp.service;

import com.pfe.webapp.dto.CompteDTO;
import com.pfe.webapp.dto.CompteResponseDTO;
import com.pfe.webapp.entity.Compte;
import com.pfe.webapp.entity.Employe;
import com.pfe.webapp.entity.StatusCompte;
import com.pfe.webapp.repository.CompteRepository;
import com.pfe.webapp.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompteManagementService {

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final int PASSWORD_LENGTH = 10;

    private String generateRandomPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = (int) (Math.random() * CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    @Transactional
    public CompteResponseDTO createCompte(CompteDTO dto) {
        Employe employe = employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employe not found"));

        if (compteRepository.findByEmployeId(dto.getEmployeId()).isPresent()) {
            throw new RuntimeException("Employee already has an account");
        }

        if (compteRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        String generatedPassword = generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(generatedPassword);

        Compte compte = new Compte();
        compte.setUsername(dto.getUsername());
        compte.setPassword(encodedPassword);
        compte.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusCompte.ACTIVE);
        compte.setEmploye(employe);

        compte = compteRepository.save(compte);

        System.out.println("\n=========================================");
        System.out.println("✅ NEW ACCOUNT CREATED");
        System.out.println("📧 Employee: " + employe.getPrenom() + " " + employe.getNom());
        System.out.println("📧 Email: " + employe.getEmail());
        System.out.println("🔑 Username: " + dto.getUsername());
        System.out.println("🔐 Password: " + generatedPassword);
        System.out.println("=========================================\n");

        return convertToResponseDTO(compte);
    }

    @Transactional(readOnly = true)
    public List<CompteResponseDTO> getAllComptes() {
        return compteRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompteResponseDTO getCompteById(Long id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return convertToResponseDTO(compte);
    }

    // ✅ أضف هذه الميثود الجديدة
    @Transactional(readOnly = true)
    public CompteResponseDTO getCompteByEmployeId(Long employeId) {
        Compte compte = compteRepository.findByEmployeId(employeId)
                .orElse(null);  // إذا لم يوجد، يرجع null بدل RuntimeException

        return compte != null ? convertToResponseDTO(compte) : null;
    }

    @Transactional
    public CompteResponseDTO updateCompteStatus(Long id, StatusCompte status) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        compte.setStatus(status);
        compte = compteRepository.save(compte);
        return convertToResponseDTO(compte);
    }

    @Transactional
    public CompteResponseDTO updateUsername(Long id, String username) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (compteRepository.findByUsername(username).isPresent() &&
                !compte.getUsername().equals(username)) {
            throw new RuntimeException("Username already exists");
        }

        compte.setUsername(username);
        compte = compteRepository.save(compte);
        return convertToResponseDTO(compte);
    }

    @Transactional
    public void resetPassword(Long id) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String newPassword = generateRandomPassword();
        compte.setPassword(passwordEncoder.encode(newPassword));
        compteRepository.save(compte);

        System.out.println("\n=========================================");
        System.out.println("🔄 PASSWORD RESET");
        System.out.println("📧 Employee: " + compte.getEmploye().getPrenom() + " " + compte.getEmploye().getNom());
        System.out.println("🔑 Username: " + compte.getUsername());
        System.out.println("🔐 New Password: " + newPassword);
        System.out.println("=========================================\n");
    }

    @Transactional
    public void deleteCompte(Long id) {
        compteRepository.deleteById(id);
    }

    private CompteResponseDTO convertToResponseDTO(Compte compte) {
        return new CompteResponseDTO(
                compte.getId(),
                compte.getUsername(),
                compte.getStatus(),
                compte.getEmploye().getNom(),
                compte.getEmploye().getPrenom(),
                compte.getEmploye().getId(),
                compte.getEmploye().getEmail()
        );
    }
}