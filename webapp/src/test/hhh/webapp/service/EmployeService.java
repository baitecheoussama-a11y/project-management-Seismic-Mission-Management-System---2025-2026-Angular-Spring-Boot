package com.pfe.webapp.service;

import com.pfe.webapp.dto.EmployeAccountDetailsDTO;
import com.pfe.webapp.dto.EmployeDTO;
import com.pfe.webapp.dto.EmployeResponseDTO;
import com.pfe.webapp.dto.EtatMedicalDTO;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.*;
import com.pfe.webapp.service.medical.EtatMedicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pfe.webapp.exception.ResourceNotFoundException;
import com.pfe.webapp.dto.fonction.FonctionSummaryDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeService {

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private EtatMedicalService etatMedicalService;

    @Autowired
    private FonctionRepository fonctionRepository;

    @Transactional
    public EmployeResponseDTO createEmploye(EmployeDTO dto) {
        // 1️⃣ Create employee
        Employe employe = new Employe();
        employe.setNom(dto.getNom());
        employe.setPrenom(dto.getPrenom());
        employe.setDateNaissance(dto.getDateNaissance());
        employe.setEmail(dto.getEmail());
        employe.setNumTel(dto.getNumTel());
        employe.setAdresse(dto.getAdresse());
        employe.setLieuNaissance(dto.getLieuNaissance());
        employe.setSexe(dto.getSexe());
        employe.setNumIdentite(dto.getNumIdentite());

        employe = employeRepository.save(employe);

        // 2️⃣ Create contract
        if (dto.getTypeContrat() != null) {
            Contrat contrat = new Contrat();
            contrat.setType(dto.getTypeContrat());
            contrat.setDateDebut(dto.getContratDateDebut());
            contrat.setDateFin(dto.getContratDateFin());
            contrat.setSalaire(dto.getSalaire());
            contrat.setDureeTravail(dto.getDureeTravail());
            contrat.setRegimeTravail(dto.getRegimeTravail());
            contrat.setEmploye(employe);
            contratRepository.save(contrat);
        }

        // 3️⃣ Create medical record (EtatMedical)
        if (dto.getGroupeSanguin() != null || dto.getAllergies() != null || dto.getMedicationsActuelles() != null) {
            EtatMedicalDTO etatMedicalDTO = new EtatMedicalDTO();
            etatMedicalDTO.setGroupeSanguin(dto.getGroupeSanguin());
            etatMedicalDTO.setAllergies(dto.getAllergies());
            etatMedicalDTO.setVaccinations(dto.getVaccinations());
            etatMedicalDTO.setMedicationsActuelles(dto.getMedicationsActuelles());
            etatMedicalDTO.setMedecinTraitant(dto.getMedecinTraitant());
            etatMedicalDTO.setDerniereVisiteMedicale(dto.getDerniereVisiteMedicale());
            etatMedicalDTO.setEmployeId(employe.getId());
            etatMedicalService.createOrUpdateEtatMedical(etatMedicalDTO);
        }

        return convertToResponseDTO(employe);
    }

    @Transactional(readOnly = true)
    public List<EmployeResponseDTO> getAllEmployes() {
        return employeRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmployeResponseDTO getEmployeById(Long id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employe not found"));
        return convertToResponseDTO(employe);
    }

    @Transactional
    public EmployeResponseDTO updateEmploye(Long id, EmployeDTO dto) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employe not found"));

        employe.setNom(dto.getNom());
        employe.setPrenom(dto.getPrenom());
        employe.setDateNaissance(dto.getDateNaissance());
        employe.setEmail(dto.getEmail());
        employe.setNumTel(dto.getNumTel());
        employe.setAdresse(dto.getAdresse());
        employe.setLieuNaissance(dto.getLieuNaissance());
        employe.setSexe(dto.getSexe());
        employe.setNumIdentite(dto.getNumIdentite());

        employe = employeRepository.save(employe);
        return convertToResponseDTO(employe);
    }

    @Transactional
    public void deleteEmploye(Long id) {
        employeRepository.deleteById(id);
    }
    private EmployeResponseDTO convertToResponseDTO(Employe employe) {
        TypeContrat typeContrat = null;
        BigDecimal salaire = null;
        String groupeSanguin = null;

        if (employe.getContrats() != null && !employe.getContrats().isEmpty()) {
            Contrat contrat = employe.getContrats().get(0);
            typeContrat = contrat.getType();
            salaire = contrat.getSalaire();
        }

        if (employe.getEtatMedical() != null) {
            groupeSanguin = employe.getEtatMedical().getGroupeSanguin();
        }

        EmployeResponseDTO dto = new EmployeResponseDTO(
                employe.getId(),
                employe.getNom(),
                employe.getPrenom(),
                employe.getDateNaissance(),
                employe.getEmail(),
                employe.getNumTel(),
                employe.getAdresse(),
                employe.getLieuNaissance(),
                employe.getSexe(),
                employe.getNumIdentite(),
                typeContrat,
                salaire,
                groupeSanguin
        );

        // ADD THIS SECTION - Set fonction information
        if (employe.getFonction() != null) {
            dto.setFonctionNom(employe.getFonction().getNom());
            dto.setFonctionId(employe.getFonction().getId());
        } else {
            dto.setFonctionNom(null);
            dto.setFonctionId(null);
        }

        return dto;
    }
    public EmployeAccountDetailsDTO getEmployeAccountDetails(Long employeId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé avec id: " + employeId));

        EmployeAccountDetailsDTO details = new EmployeAccountDetailsDTO();

        // Employee info
        details.setId(employe.getId());
        details.setNom(employe.getNom());
        details.setPrenom(employe.getPrenom());
        details.setEmail(employe.getEmail());
        details.setNumTel(employe.getNumTel());
        details.setAdresse(employe.getAdresse());
        details.setNumIdentite(employe.getNumIdentite());
        details.setDateNaissance(employe.getDateNaissance());
        details.setLieuNaissance(employe.getLieuNaissance());
        details.setSexe(employe.getSexe() != null ? employe.getSexe().toString() : "N/A");

        // Account info
        if (employe.getCompte() != null) {
            details.setUsername(employe.getCompte().getUsername());
            details.setCompteStatus(employe.getCompte().getStatus() != null ?
                    employe.getCompte().getStatus().toString() : "INACTIF");
        } else {
            details.setUsername("Aucun compte");
            details.setCompteStatus("NON EXISTANT");
        }

        // Contracts
        List<EmployeAccountDetailsDTO.ContratInfoDTO> contratsDTO = new ArrayList<>();
        if (employe.getContrats() != null) {
            for (Contrat contrat : employe.getContrats()) {
                EmployeAccountDetailsDTO.ContratInfoDTO c = new EmployeAccountDetailsDTO.ContratInfoDTO();
                c.setId(contrat.getId());
                c.setType(contrat.getType() != null ? contrat.getType().toString() : "N/A");
                c.setDateDebut(contrat.getDateDebut());
                c.setDateFin(contrat.getDateFin());
                c.setSalaire(contrat.getSalaire() != null ? contrat.getSalaire().toString() : "N/A");
                c.setDureeTravail(contrat.getDureeTravail());
                c.setRegimeTravail(contrat.getRegimeTravail());
                contratsDTO.add(c);
            }
        }
        details.setContrats(contratsDTO);

        // Medical record (EtatMedical)
        if (employe.getEtatMedical() != null) {
            EtatMedical em = employe.getEtatMedical();
            EmployeAccountDetailsDTO.DossierMedicalInfoDTO emDTO = new EmployeAccountDetailsDTO.DossierMedicalInfoDTO();
            emDTO.setGroupeSanguin(em.getGroupeSanguin());
            emDTO.setAntecedentsMedicaux(em.getAntecedentsMedicaux() != null ?
                    em.getAntecedentsMedicaux().toString() : null);
            emDTO.setAllergies(em.getAllergies());
            emDTO.setVaccinations(em.getVaccinations());
            emDTO.setMedicationsActuelles(em.getMedicationsActuelles());
            emDTO.setMedecinTraitant(em.getMedecinTraitant());
            emDTO.setDerniereVisiteMedicale(em.getDerniereVisiteMedicale());
            details.setDossierMedical(emDTO);
        } else {
            details.setDossierMedical(null);
        }

        // Roles
        List<EmployeAccountDetailsDTO.RoleInfoDTO> rolesDTO = new ArrayList<>();
        if (employe.getCompte() != null && employe.getCompte().getRoles() != null) {
            for (AffectationRole ar : employe.getCompte().getRoles()) {
                if (ar.getRole() != null) {
                    EmployeAccountDetailsDTO.RoleInfoDTO r = new EmployeAccountDetailsDTO.RoleInfoDTO();
                    r.setId(ar.getRole().getId());
                    r.setName(ar.getRole().getName());
                    r.setType(ar.getRole().getType() != null ? ar.getRole().getType().toString() : "AUTRE");
                    r.setDateDebut(ar.getDateDebut());
                    r.setDateFin(ar.getDateFin());
                    r.setActive(ar.isActive());
                    rolesDTO.add(r);
                }
            }
        }
        details.setRoles(rolesDTO);

        // ADD THIS: Fonction information
        if (employe.getFonction() != null) {
            EmployeAccountDetailsDTO.FonctionInfoDTO fonctionInfo = new EmployeAccountDetailsDTO.FonctionInfoDTO();
            fonctionInfo.setId(employe.getFonction().getId());
            fonctionInfo.setNom(employe.getFonction().getNom());
            fonctionInfo.setDescription(employe.getFonction().getDescription());
            fonctionInfo.setNombreEmployes(employe.getFonction().getEmployes() != null ?
                    employe.getFonction().getEmployes().size() : 0);
            details.setFonction(fonctionInfo);
        } else {
            details.setFonction(null);
        }

        return details;
    }



    // Add the convertToDTO method
    private EmployeDTO convertToDTO(Employe employe) {
        EmployeDTO dto = new EmployeDTO();
        dto.setId(employe.getId());
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());
        dto.setDateNaissance(employe.getDateNaissance());
        dto.setEmail(employe.getEmail());
        dto.setNumTel(employe.getNumTel());
        dto.setAdresse(employe.getAdresse());
        dto.setLieuNaissance(employe.getLieuNaissance());
        dto.setSexe(employe.getSexe());
        dto.setNumIdentite(employe.getNumIdentite());


        // Set fonction information
        if (employe.getFonction() != null) {
            FonctionSummaryDTO fonctionSummary = new FonctionSummaryDTO();
            fonctionSummary.setId(employe.getFonction().getId());
            fonctionSummary.setNom(employe.getFonction().getNom());
            fonctionSummary.setDescription(employe.getFonction().getDescription());
            dto.setFonction(fonctionSummary);
        }

        return dto;
    }

    // Add the assign and remove methods
    public EmployeDTO assignFonctionToEmploye(Long employeId, Long fonctionId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employe not found with id: " + employeId));

        Fonction fonction = fonctionRepository.findById(fonctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Fonction not found with id: " + fonctionId));

        employe.setFonction(fonction);
        Employe updatedEmploye = employeRepository.save(employe);

        return convertToDTO(updatedEmploye);
    }

    public EmployeDTO removeFonctionFromEmploye(Long employeId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employe not found with id: " + employeId));

        employe.setFonction(null);
        Employe updatedEmploye = employeRepository.save(employe);

        return convertToDTO(updatedEmploye);
    }




    // EmployeService.java - Add this method
    @Transactional(readOnly = true)
    public List<EmployeResponseDTO> getEmployesByFonction(Long fonctionId) {
        List<Employe> employes = employeRepository.findByFonctionId(fonctionId);
        return employes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

}