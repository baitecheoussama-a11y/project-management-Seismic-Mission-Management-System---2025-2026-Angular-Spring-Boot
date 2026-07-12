// CategorieMaterielService.java
package com.pfe.webapp.service;

import com.pfe.webapp.entity.materiel.CategorieMateriel;
import com.pfe.webapp.repository.CategorieMaterielRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class CategorieMaterielService {

    @Autowired
    private CategorieMaterielRepository categorieRepository;

    public List<CategorieMateriel> getAllCategories() {
        return categorieRepository.findAll();
    }

    public CategorieMateriel getCategorieById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée"));
    }

    public CategorieMateriel createCategorie(CategorieMateriel categorie) {
        return categorieRepository.save(categorie);
    }

    public CategorieMateriel updateCategorie(Long id, CategorieMateriel categorieDetails) {
        CategorieMateriel categorie = getCategorieById(id);
        categorie.setNom(categorieDetails.getNom());
        return categorieRepository.save(categorie);
    }

    @Transactional
    public void deleteCategorie(Long id) {
        // ✅ حذف جميع الـ Types المرتبطة أولاً
        categorieRepository.deleteTypesByCategoryId(id);
        // ✅ ثم حذف الـ Category
        categorieRepository.deleteById(id);
    }
}