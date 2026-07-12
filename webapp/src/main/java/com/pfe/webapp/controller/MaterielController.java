package com.pfe.webapp.controller;

import com.pfe.webapp.dto.MaterielDTO;
import com.pfe.webapp.entity.StatusMateriel;
import com.pfe.webapp.entity.materiel.Materiel;
import com.pfe.webapp.service.MaterielService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/materiels")
@CrossOrigin(origins = "http://localhost:4200")

public class MaterielController {

    @Autowired
    private MaterielService materielService;

    @GetMapping
    public ResponseEntity<List<MaterielDTO>> getAllMateriels() {
        return ResponseEntity.ok(materielService.getAllMateriels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterielDTO> getMaterielById(@PathVariable Long id) {
        return ResponseEntity.ok(materielService.getMaterielById(id));
    }

    @PostMapping
    public ResponseEntity<MaterielDTO> createMateriel(@RequestBody MaterielDTO materielDTO) {
        return new ResponseEntity<>(materielService.createMateriel(materielDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterielDTO> updateMateriel(@PathVariable Long id, @RequestBody MaterielDTO materielDTO) {
        return ResponseEntity.ok(materielService.updateMateriel(id, materielDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMateriel(@PathVariable Long id) {
        materielService.deleteMateriel(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MaterielDTO> updateStatus(@PathVariable Long id, @RequestParam StatusMateriel status) {
        MaterielDTO updated = materielService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<MaterielDTO>> getMaterielsByCategorie(@PathVariable Long categorieId) {
        List<Materiel> materiels = materielService.getMaterielsByCategorie(categorieId);
        List<MaterielDTO> dtos = materiels.stream()
                .map(materielService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<MaterielDTO>> getMaterielsByType(@PathVariable Long typeId) {
        List<Materiel> materiels = materielService.getMaterielsByType(typeId);
        List<MaterielDTO> dtos = materiels.stream()
                .map(materielService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/count/by-category/{categoryId}")
    public ResponseEntity<Long> countByCategory(@PathVariable Long categoryId) {
        long count = materielService.countByCategory(categoryId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/by-type/{typeId}")
    public ResponseEntity<Long> countByType(@PathVariable Long typeId) {
        long count = materielService.countByType(typeId);
        return ResponseEntity.ok(count);
    }
}