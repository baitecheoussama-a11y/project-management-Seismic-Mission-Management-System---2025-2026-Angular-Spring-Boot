// FonctionController.java
package com.pfe.webapp.controller;

import com.pfe.webapp.dto.fonction.CreateFonctionDTO;
import com.pfe.webapp.dto.fonction.FonctionDTO;
import com.pfe.webapp.dto.fonction.UpdateFonctionDTO;
import com.pfe.webapp.service.FonctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fonctions")
@CrossOrigin(origins = "*")
public class FonctionController {

    @Autowired
    private FonctionService fonctionService;

    @GetMapping
    public ResponseEntity<List<FonctionDTO>> getAllFonctions() {
        return ResponseEntity.ok(fonctionService.getAllFonctions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FonctionDTO> getFonctionById(@PathVariable Long id) {
        return ResponseEntity.ok(fonctionService.getFonctionById(id));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<FonctionDTO> getFonctionWithEmployes(@PathVariable Long id) {
        return ResponseEntity.ok(fonctionService.getFonctionWithEmployes(id));
    }

    @PostMapping
    public ResponseEntity<FonctionDTO> createFonction(@RequestBody CreateFonctionDTO createDTO) {
        FonctionDTO createdFonction = fonctionService.createFonction(createDTO);
        return new ResponseEntity<>(createdFonction, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FonctionDTO> updateFonction(
            @PathVariable Long id,
            @RequestBody UpdateFonctionDTO updateDTO) {
        FonctionDTO updatedFonction = fonctionService.updateFonction(id, updateDTO);
        return ResponseEntity.ok(updatedFonction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFonction(@PathVariable Long id) {
        fonctionService.deleteFonction(id);
        return ResponseEntity.noContent().build();
    }
}