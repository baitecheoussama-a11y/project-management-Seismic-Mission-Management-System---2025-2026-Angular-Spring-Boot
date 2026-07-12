package com.pfe.webapp.service;

import com.pfe.webapp.dto.event.TypeEvenementDTO;
import com.pfe.webapp.dto.event.TypeEvenementRequestDTO;
import com.pfe.webapp.entity.TypeEvenement;
import com.pfe.webapp.entity.NiveauPriorite;
import com.pfe.webapp.repository.event.TypeEvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TypeEvenementServiceImpl implements TypeEvenementService {

    @Autowired
    private TypeEvenementRepository typeEvenementRepository;

    // Helper method to convert Entity to DTO
    private TypeEvenementDTO convertToDTO(TypeEvenement typeEvenement) {
        if (typeEvenement == null) return null;

        TypeEvenementDTO dto = new TypeEvenementDTO();
        dto.setId(typeEvenement.getId());
        dto.setNom(typeEvenement.getNom());
        dto.setDescription(typeEvenement.getDescription());
        dto.setActif(typeEvenement.getActif());

        if (typeEvenement.getNiveauPriorite() != null) {
            dto.setNiveauPriorite(typeEvenement.getNiveauPriorite().name());
            dto.setNiveauPrioriteLabel(typeEvenement.getNiveauPriorite().getLabel());
            dto.setNiveauPrioriteColor(typeEvenement.getNiveauPriorite().getColor());
        }

        dto.setEvenementsCount(typeEvenement.getEvenements() != null ?
                typeEvenement.getEvenements().size() : 0);

        return dto;
    }

    @Override
    public TypeEvenementDTO createTypeEvenement(TypeEvenementRequestDTO requestDTO) {
        // Check if name already exists
        if (existsByNom(requestDTO.getNom())) {
            throw new RuntimeException("Type name already exists: " + requestDTO.getNom());
        }

        TypeEvenement typeEvenement = new TypeEvenement();
        typeEvenement.setNom(requestDTO.getNom());
        typeEvenement.setDescription(requestDTO.getDescription());
        typeEvenement.setActif(requestDTO.getActif() != null ? requestDTO.getActif() : true);

        if (requestDTO.getNiveauPriorite() != null) {
            typeEvenement.setNiveauPriorite(NiveauPriorite.valueOf(requestDTO.getNiveauPriorite()));
        }

        TypeEvenement savedType = typeEvenementRepository.save(typeEvenement);
        return convertToDTO(savedType);
    }

    @Override
    public TypeEvenementDTO updateTypeEvenement(Long id, TypeEvenementRequestDTO requestDTO) {
        TypeEvenement existingType = typeEvenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type not found with id: " + id));

        existingType.setNom(requestDTO.getNom());
        existingType.setDescription(requestDTO.getDescription());

        if (requestDTO.getNiveauPriorite() != null) {
            existingType.setNiveauPriorite(NiveauPriorite.valueOf(requestDTO.getNiveauPriorite()));
        }

        TypeEvenement updatedType = typeEvenementRepository.save(existingType);
        return convertToDTO(updatedType);
    }

    @Override
    public void deleteTypeEvenement(Long id) {
        if (!typeEvenementRepository.existsById(id)) {
            throw new RuntimeException("Type not found with id: " + id);
        }
        typeEvenementRepository.deleteById(id);
    }

    @Override
    public TypeEvenementDTO getTypeEvenementById(Long id) {
        TypeEvenement type = typeEvenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type not found with id: " + id));
        return convertToDTO(type);
    }

    @Override
    public List<TypeEvenementDTO> getAllTypeEvenements() {
        return typeEvenementRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TypeEvenementDTO> getActiveTypeEvenements() {
        return typeEvenementRepository.findByActifTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TypeEvenementDTO> getInactiveTypeEvenements() {
        return typeEvenementRepository.findByActifFalse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TypeEvenementDTO> searchTypeEvenements(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllTypeEvenements();
        }
        return typeEvenementRepository.searchByName(keyword.trim()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TypeEvenementDTO toggleTypeEvenementStatus(Long id) {
        TypeEvenement type = typeEvenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type not found with id: " + id));

        type.setActif(!type.getActif());
        TypeEvenement updatedType = typeEvenementRepository.save(type);
        return convertToDTO(updatedType);
    }

    @Override
    public boolean existsByNom(String nom) {
        return typeEvenementRepository.existsByNom(nom);
    }
    @Override
    public TypeEvenementDTO updateStatus(Long id, Boolean actif) {
        TypeEvenement type = typeEvenementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Type not found with id: " + id));

        type.setActif(actif);
        TypeEvenement updatedType = typeEvenementRepository.save(type);
        return convertToDTO(updatedType);
    }
}