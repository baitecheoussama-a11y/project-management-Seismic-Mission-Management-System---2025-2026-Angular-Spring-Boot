package com.pfe.webapp.service;

import com.pfe.webapp.entity.materiel.CategorieMateriel;
import com.pfe.webapp.entity.materiel.TypeMateriel;
import com.pfe.webapp.repository.CategorieMaterielRepository;
import com.pfe.webapp.repository.TypeMaterielRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class TypeMaterielService {

    @Autowired
    private TypeMaterielRepository typeRepository;

    @Autowired
    private CategorieMaterielRepository categorieRepository;

    public List<TypeMateriel> getAllTypes() {
        return typeRepository.findAll();
    }

    public TypeMateriel getTypeById(Long id) {
        return typeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Type non trouvé avec id: " + id));
    }

    public List<TypeMateriel> getTypesByCategorie(Long categorieId) {
        return typeRepository.findByCategorieIdCategorie(categorieId);
    }

    public TypeMateriel createType(TypeMateriel type) {
        // ✅ تأكد من وجود الـ Category
        if (type.getCategorie() == null || type.getCategorie().getIdCategorie() == null) {
            throw new IllegalArgumentException("Category ID is required to create a type");
        }

        // ✅ ابحث عن الـ Category
        CategorieMateriel categorie = categorieRepository.findById(type.getCategorie().getIdCategorie())
                .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée avec id: " + type.getCategorie().getIdCategorie()));

        // ✅ ربط الـ Type بالـ Category
        type.setCategorie(categorie);

        // ✅ حفظ
        return typeRepository.save(type);
    }

    public TypeMateriel updateType(Long id, TypeMateriel typeDetails) {
        TypeMateriel type = getTypeById(id);
        type.setLibelle(typeDetails.getLibelle());

        if (typeDetails.getCategorie() != null && typeDetails.getCategorie().getIdCategorie() != null) {
            CategorieMateriel categorie = categorieRepository.findById(typeDetails.getCategorie().getIdCategorie())
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée avec id: " + typeDetails.getCategorie().getIdCategorie()));
            type.setCategorie(categorie);
        }

        return typeRepository.save(type);
    }

    public void deleteType(Long id) {
        // ✅ تأكد من وجود الـ Type قبل الحذف
        if (!typeRepository.existsById(id)) {
            throw new EntityNotFoundException("Type non trouvé avec id: " + id);
        }
        typeRepository.deleteById(id);
    }
}