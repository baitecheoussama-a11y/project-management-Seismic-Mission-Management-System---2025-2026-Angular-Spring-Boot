package com.pfe.webapp.service;

import com.pfe.webapp.entity.ressource.*;
import com.pfe.webapp.repository.ressource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
public class RessourceService {

    @Autowired
    private CategorieRessourceRepository categorieRepository;

    @Autowired
    private TypeRessourceRepository typeRepository;

    @Autowired
    private RessourceRepository ressourceRepository;

    // ==================== CATEGORY CRUD ====================
    public List<CategorieRessource> getAllCategories() {
        return categorieRepository.findAll();
    }

    public CategorieRessource getCategoryById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    public CategorieRessource createCategory(CategorieRessource category) {
        return categorieRepository.save(category);
    }

    public CategorieRessource updateCategory(Long id, CategorieRessource category) {
        CategorieRessource existing = getCategoryById(id);
        existing.setNom(category.getNom());
        return categorieRepository.save(existing);
    }

    public void deleteCategory(Long id) {
        categorieRepository.deleteById(id);
    }

    // ==================== TYPE CRUD ====================
    public List<TypeRessource> getAllTypes() {
        return typeRepository.findAll();
    }

    public List<TypeRessource> getTypesByCategory(Long categorieId) {
        return typeRepository.findByCategorieRessourceIdCategorieRessource(categorieId);
    }

    public TypeRessource getTypeById(Long id) {
        return typeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Type not found"));
    }

    public TypeRessource createType(TypeRessource type, Long categorieId) {
        CategorieRessource category = getCategoryById(categorieId);
        type.setCategorieRessource(category);
        return typeRepository.save(type);
    }

    public TypeRessource updateType(Long id, TypeRessource type) {
        TypeRessource existing = getTypeById(id);
        existing.setNom(type.getNom());
        return typeRepository.save(existing);
    }

    public void deleteType(Long id) {
        typeRepository.deleteById(id);
    }

    // ==================== RESSOURCE CRUD ====================
    public List<Ressource> getAllRessources() {
        return ressourceRepository.findAll();
    }

    public List<Ressource> getRessourcesByCategory(Long categorieId) {
        return ressourceRepository.findByTypeRessource_CategorieRessource_IdCategorieRessource(categorieId);
    }

    public List<Ressource> getRessourcesByType(Long typeId) {
        return ressourceRepository.findByTypeRessourceIdTypeRessource(typeId);
    }

    public Ressource getRessourceById(Long id) {
        return ressourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ressource not found"));
    }

    public Ressource createRessource(Ressource ressource, Long typeId) {
        TypeRessource type = getTypeById(typeId);
        ressource.setTypeRessource(type);
        return ressourceRepository.save(ressource);
    }

    public Ressource updateRessource(Long id, Ressource ressource) {
        Ressource existing = getRessourceById(id);
        existing.setTitre(ressource.getTitre());
        existing.setDescription(ressource.getDescription());
        existing.setQuantite(ressource.getQuantite());
        existing.setUnite(ressource.getUnite());
        existing.setCout(ressource.getCout());
        existing.setDateAchat(ressource.getDateAchat());
        return ressourceRepository.save(existing);
    }

    public void deleteRessource(Long id) {
        ressourceRepository.deleteById(id);
    }

    public long countByCategory(Long categoryId) {
        return ressourceRepository.findByTypeRessource_CategorieRessource_IdCategorieRessource(categoryId).size();
    }

    public long countByType(Long typeId) {
        return ressourceRepository.findByTypeRessourceIdTypeRessource(typeId).size();
    }
}