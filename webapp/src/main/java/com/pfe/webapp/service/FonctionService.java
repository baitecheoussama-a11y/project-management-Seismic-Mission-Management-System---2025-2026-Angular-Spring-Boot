// FonctionService.java
package com.pfe.webapp.service;

import com.pfe.webapp.dto.fonction.CreateFonctionDTO;
import com.pfe.webapp.dto.fonction.EmployeSummaryDTO;
import com.pfe.webapp.dto.fonction.FonctionDTO;
import com.pfe.webapp.dto.fonction.UpdateFonctionDTO;
import com.pfe.webapp.entity.Employe;
import com.pfe.webapp.entity.Fonction;
import com.pfe.webapp.exception.ResourceNotFoundException;
import com.pfe.webapp.repository.EmployeRepository;
import com.pfe.webapp.repository.FonctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FonctionService {

    @Autowired
    private FonctionRepository fonctionRepository;

    @Autowired
    private EmployeRepository employeRepository;  // Add this

    public List<FonctionDTO> getAllFonctions() {
        return fonctionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FonctionDTO getFonctionById(Long id) {
        Fonction fonction = fonctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fonction not found with id: " + id));
        return convertToDTO(fonction);
    }

    public FonctionDTO getFonctionWithEmployes(Long id) {
        Fonction fonction = fonctionRepository.findByIdWithEmployes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fonction not found with id: " + id));
        return convertToDTOWithEmployes(fonction);
    }

    public FonctionDTO createFonction(CreateFonctionDTO createDTO) {
        if (fonctionRepository.existsByNom(createDTO.getNom())) {
            throw new RuntimeException("Fonction with name '" + createDTO.getNom() + "' already exists");
        }

        Fonction fonction = new Fonction();
        fonction.setNom(createDTO.getNom());
        fonction.setDescription(createDTO.getDescription());

        Fonction savedFonction = fonctionRepository.save(fonction);
        return convertToDTO(savedFonction);
    }

    public FonctionDTO updateFonction(Long id, UpdateFonctionDTO updateDTO) {
        Fonction fonction = fonctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fonction not found with id: " + id));

        if (updateDTO.getNom() != null && !updateDTO.getNom().equals(fonction.getNom())) {
            if (fonctionRepository.existsByNom(updateDTO.getNom())) {
                throw new RuntimeException("Fonction with name '" + updateDTO.getNom() + "' already exists");
            }
            fonction.setNom(updateDTO.getNom());
        }

        if (updateDTO.getDescription() != null) {
            fonction.setDescription(updateDTO.getDescription());
        }

        Fonction updatedFonction = fonctionRepository.save(fonction);
        return convertToDTO(updatedFonction);
    }

    public void deleteFonction(Long id) {
        Fonction fonction = fonctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fonction not found with id: " + id));

        if (!fonction.getEmployes().isEmpty()) {
            throw new RuntimeException("Cannot delete fonction with assigned employees");
        }

        fonctionRepository.delete(fonction);
    }

    private FonctionDTO convertToDTO(Fonction fonction) {
        FonctionDTO dto = new FonctionDTO();
        dto.setId(fonction.getId());
        dto.setNom(fonction.getNom());
        dto.setDescription(fonction.getDescription());
        dto.setNombreEmployes(fonction.getEmployes().size());
        return dto;
    }

    private FonctionDTO convertToDTOWithEmployes(Fonction fonction) {
        FonctionDTO dto = convertToDTO(fonction);

        List<EmployeSummaryDTO> employeSummaries = fonction.getEmployes().stream()
                .map(this::convertEmployeToSummary)
                .collect(Collectors.toList());

        dto.setEmployes(employeSummaries);
        return dto;
    }

    private EmployeSummaryDTO convertEmployeToSummary(Employe employe) {
        EmployeSummaryDTO summary = new EmployeSummaryDTO();
        summary.setId(employe.getId());
        summary.setNom(employe.getNom());
        summary.setPrenom(employe.getPrenom());
        summary.setNumIdentite(employe.getNumIdentite());
        summary.setEmail(employe.getEmail());
        return summary;
    }
}