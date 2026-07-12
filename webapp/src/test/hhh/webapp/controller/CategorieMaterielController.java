package com.pfe.webapp.controller;

import com.pfe.webapp.dto.CategorieMaterielDTO;
import com.pfe.webapp.entity.materiel.CategorieMateriel;
import com.pfe.webapp.service.CategorieMaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories-materiel")
@CrossOrigin(origins = "http://localhost:4200")

public class CategorieMaterielController {

    @Autowired
    private CategorieMaterielService categorieService;

    @GetMapping
    public ResponseEntity<List<CategorieMaterielDTO>> getAllCategories() {
        List<CategorieMateriel> categories = categorieService.getAllCategories();
        List<CategorieMaterielDTO> dtos = categories.stream()
                .map(c -> new CategorieMaterielDTO(c.getIdCategorie(), c.getNom()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieMaterielDTO> getCategorieById(@PathVariable Long id) {
        CategorieMateriel categorie = categorieService.getCategorieById(id);
        CategorieMaterielDTO dto = new CategorieMaterielDTO(categorie.getIdCategorie(), categorie.getNom());
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<CategorieMaterielDTO> createCategorie(@RequestBody CategorieMaterielDTO dto) {
        CategorieMateriel categorie = new CategorieMateriel();
        categorie.setNom(dto.getNom());
        CategorieMateriel saved = categorieService.createCategorie(categorie);
        CategorieMaterielDTO responseDto = new CategorieMaterielDTO(saved.getIdCategorie(), saved.getNom());
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategorieMaterielDTO> updateCategorie(@PathVariable Long id, @RequestBody CategorieMaterielDTO dto) {
        CategorieMateriel categorie = new CategorieMateriel();
        categorie.setNom(dto.getNom());
        CategorieMateriel updated = categorieService.updateCategorie(id, categorie);
        CategorieMaterielDTO responseDto = new CategorieMaterielDTO(updated.getIdCategorie(), updated.getNom());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }
}