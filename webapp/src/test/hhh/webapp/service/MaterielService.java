package com.pfe.webapp.service;

import com.pfe.webapp.dto.MaterielDTO;
import com.pfe.webapp.dto.MaterielImageDTO;
import com.pfe.webapp.entity.materiel.*;
import com.pfe.webapp.entity.StatusMateriel;
import com.pfe.webapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterielService {

    @Autowired
    private MaterielRepository materielRepository;

    @Autowired
    private TypeMaterielRepository typeMaterielRepository;

    @Autowired
    private MaterielImageRepository imageRepository;

    @Autowired
    private HistoriqueUtilisationRepository historiqueRepository;

    @Autowired
    private ReparationRepository reparationRepository;

    @Autowired
    private AffectationMaterielRepository affectationRepository;

    public List<MaterielDTO> getAllMateriels() {
        return materielRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MaterielDTO getMaterielById(Long id) {
        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Materiel non trouvé avec id: " + id));
        return convertToDTO(materiel);
    }

    @Transactional
    public MaterielDTO createMateriel(MaterielDTO dto) {
        Materiel materiel = new Materiel();
        materiel.setCodeMateriel(dto.getCodeMateriel());
        materiel.setMarque(dto.getMarque());
        materiel.setModele(dto.getModele());
        materiel.setDesignation(dto.getDesignation());
        materiel.setDateAchat(dto.getDateAchat());
        materiel.setPrix(dto.getPrix());

        // Set status from DTO or default to EN_BON_ETAT
        if (dto.getStatus() != null) {
            materiel.setStatus(dto.getStatus());
        } else {
            materiel.setStatus(StatusMateriel.EN_BON_ETAT);
        }

        if (dto.getTypeMaterielId() != null) {
            TypeMateriel type = typeMaterielRepository.findById(dto.getTypeMaterielId())
                    .orElseThrow(() -> new EntityNotFoundException("Type non trouvé avec id: " + dto.getTypeMaterielId()));
            materiel.setTypeMateriel(type);
        }

        Materiel saved = materielRepository.save(materiel);
        return convertToDTO(saved);
    }

    @Transactional
    public MaterielDTO updateMateriel(Long id, MaterielDTO dto) {
        Materiel materiel = getMaterielEntityById(id);

        // Update basic fields
        if (dto.getCodeMateriel() != null) {
            materiel.setCodeMateriel(dto.getCodeMateriel());
        }
        if (dto.getMarque() != null) {
            materiel.setMarque(dto.getMarque());
        }
        if (dto.getModele() != null) {
            materiel.setModele(dto.getModele());
        }
        if (dto.getDesignation() != null) {
            materiel.setDesignation(dto.getDesignation());
        }
        if (dto.getDateAchat() != null) {
            materiel.setDateAchat(dto.getDateAchat());
        }
        if (dto.getPrix() != null) {
            materiel.setPrix(dto.getPrix());
        }

        // Update status if provided
        if (dto.getStatus() != null) {
            materiel.setStatus(dto.getStatus());
        }

        // Update type if provided
        if (dto.getTypeMaterielId() != null) {
            TypeMateriel type = typeMaterielRepository.findById(dto.getTypeMaterielId())
                    .orElseThrow(() -> new EntityNotFoundException("Type non trouvé avec id: " + dto.getTypeMaterielId()));
            materiel.setTypeMateriel(type);
        }

        Materiel updated = materielRepository.save(materiel);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteMateriel(Long id) {
        Materiel materiel = getMaterielEntityById(id);

        // Check if materiel has active assignments
        List<AffectationMateriel> activeAssignments = affectationRepository.findActiveByMaterielId(id);
        if (!activeAssignments.isEmpty()) {
            throw new IllegalStateException("لا يمكن حذف الماتيريال لأنه مرتبط بتعيينات نشطة لمهمات");
        }

        // Delete all images first
        imageRepository.deleteByMaterielIdMateriel(id);
        // Delete all affectations (should be empty but just in case)
        affectationRepository.deleteByMaterielIdMateriel(id);
        materielRepository.delete(materiel);
    }

    @Transactional
    public MaterielDTO updateStatus(Long id, StatusMateriel newStatus) {
        Materiel materiel = getMaterielEntityById(id);
        materiel.setStatus(newStatus);
        Materiel updated = materielRepository.save(materiel);
        return convertToDTO(updated);
    }

    public List<Materiel> getMaterielsByCategorie(Long categorieId) {
        return materielRepository.findByTypeMateriel_Categorie_IdCategorie(categorieId);
    }

    public List<Materiel> getMaterielsByType(Long typeId) {
        return materielRepository.findByTypeMateriel_IdTypeMateriel(typeId);
    }

    private Materiel getMaterielEntityById(Long id) {
        return materielRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Materiel non trouvé avec id: " + id));
    }

    public MaterielDTO convertToDTO(Materiel materiel) {
        MaterielDTO dto = new MaterielDTO();
        dto.setIdMateriel(materiel.getIdMateriel());
        dto.setCodeMateriel(materiel.getCodeMateriel());
        dto.setMarque(materiel.getMarque());
        dto.setModele(materiel.getModele());
        dto.setDesignation(materiel.getDesignation());
        dto.setDateAchat(materiel.getDateAchat());
        dto.setPrix(materiel.getPrix());
        dto.setStatus(materiel.getStatus());

        // Calculate dynamic utilization status
        dto.setEnUtilisation(materiel.isEnUtilisation());

        // Calculate total repair costs
        if (materiel.getReparations() != null) {
            Double totalRepairCost = materiel.getReparations().stream()
                    .map(Reparation::getCout)
                    .filter(cout -> cout != null)
                    .reduce(0.0, Double::sum);
            dto.setCoutTotalReparations(totalRepairCost);
        } else {
            dto.setCoutTotalReparations(0.0);
        }

        if (materiel.getTypeMateriel() != null) {
            dto.setTypeMaterielId(materiel.getTypeMateriel().getIdTypeMateriel());
            dto.setTypeMaterielLibelle(materiel.getTypeMateriel().getLibelle());
            if (materiel.getTypeMateriel().getCategorie() != null) {
                dto.setCategorieId(materiel.getTypeMateriel().getCategorie().getIdCategorie());
                dto.setCategorieNom(materiel.getTypeMateriel().getCategorie().getNom());
            }
        }

        if (materiel.getImages() != null) {
            dto.setImages(materiel.getImages().stream()
                    .map(this::convertImageToDTO)
                    .collect(Collectors.toList()));
        }

        dto.setAffectationCount(materiel.getAffectations() != null ? materiel.getAffectations().size() : 0);
        dto.setReparationCount(materiel.getReparations() != null ? materiel.getReparations().size() : 0);

        return dto;
    }

    private MaterielImageDTO convertImageToDTO(MaterielImage image) {
        MaterielImageDTO dto = new MaterielImageDTO();
        dto.setIdImage(image.getIdImage());
        dto.setImageUrl(image.getImageUrl());
        dto.setFileName(image.getFileName());
        dto.setContentType(image.getContentType());
        return dto;
    }

    public long countByCategory(Long categoryId) {
        return materielRepository.countByTypeMateriel_Categorie_IdCategorie(categoryId);
    }

    public long countByType(Long typeId) {
        return materielRepository.countByTypeMateriel_IdTypeMateriel(typeId);
    }
}