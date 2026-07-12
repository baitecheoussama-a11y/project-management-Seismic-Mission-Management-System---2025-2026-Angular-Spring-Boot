package com.pfe.webapp.controller;

import com.pfe.webapp.dto.TypeMaterielDTO;
import com.pfe.webapp.entity.materiel.CategorieMateriel;
import com.pfe.webapp.entity.materiel.TypeMateriel;
import com.pfe.webapp.service.TypeMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/types-materiel")
@CrossOrigin(origins = "http://localhost:4200")

public class TypeMaterielController {

    @Autowired
    private TypeMaterielService typeService;

    @GetMapping
    public ResponseEntity<List<TypeMaterielDTO>> getAllTypes() {
        List<TypeMateriel> types = typeService.getAllTypes();
        List<TypeMaterielDTO> dtos = types.stream()
                .map(t -> new TypeMaterielDTO(
                        t.getIdTypeMateriel(),
                        t.getLibelle(),
                        t.getCategorie() != null ? t.getCategorie().getIdCategorie() : null,
                        t.getCategorie() != null ? t.getCategorie().getNom() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<TypeMaterielDTO>> getTypesByCategorie(@PathVariable Long categorieId) {
        List<TypeMateriel> types = typeService.getTypesByCategorie(categorieId);
        List<TypeMaterielDTO> dtos = types.stream()
                .map(t -> new TypeMaterielDTO(
                        t.getIdTypeMateriel(),
                        t.getLibelle(),
                        t.getCategorie() != null ? t.getCategorie().getIdCategorie() : null,
                        t.getCategorie() != null ? t.getCategorie().getNom() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<TypeMaterielDTO> createType(@RequestBody TypeMaterielDTO dto) {
        TypeMateriel type = new TypeMateriel();
        type.setLibelle(dto.getLibelle());

        if (dto.getCategorieId() != null) {
            CategorieMateriel categorie = new CategorieMateriel();
            categorie.setIdCategorie(dto.getCategorieId());
            type.setCategorie(categorie);
        }

        TypeMateriel saved = typeService.createType(type);
        TypeMaterielDTO responseDto = new TypeMaterielDTO(
                saved.getIdTypeMateriel(),
                saved.getLibelle(),
                saved.getCategorie() != null ? saved.getCategorie().getIdCategorie() : null,
                saved.getCategorie() != null ? saved.getCategorie().getNom() : null
        );
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeMaterielDTO> updateType(@PathVariable Long id, @RequestBody TypeMaterielDTO dto) {
        TypeMateriel type = new TypeMateriel();
        type.setLibelle(dto.getLibelle());

        if (dto.getCategorieId() != null) {
            CategorieMateriel categorie = new CategorieMateriel();
            categorie.setIdCategorie(dto.getCategorieId());
            type.setCategorie(categorie);
        }

        TypeMateriel updated = typeService.updateType(id, type);
        TypeMaterielDTO responseDto = new TypeMaterielDTO(
                updated.getIdTypeMateriel(),
                updated.getLibelle(),
                updated.getCategorie() != null ? updated.getCategorie().getIdCategorie() : null,
                updated.getCategorie() != null ? updated.getCategorie().getNom() : null
        );
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        typeService.deleteType(id);
        return ResponseEntity.noContent().build();
    }
}