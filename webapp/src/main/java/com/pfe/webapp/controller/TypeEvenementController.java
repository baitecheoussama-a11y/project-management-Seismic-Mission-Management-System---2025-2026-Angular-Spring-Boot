package com.pfe.webapp.controller;

import com.pfe.webapp.dto.event.TypeEvenementDTO;
import com.pfe.webapp.dto.event.TypeEvenementRequestDTO;
import com.pfe.webapp.service.TypeEvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/type-evenements")
@CrossOrigin(origins = "http://localhost:4200")
public class TypeEvenementController {

    @Autowired
    private TypeEvenementService typeEvenementService;

    // ========== CREATE ==========
    @PostMapping
    public ResponseEntity<TypeEvenementDTO> createTypeEvenement(@RequestBody TypeEvenementRequestDTO requestDTO) {
        try {
            TypeEvenementDTO createdType = typeEvenementService.createTypeEvenement(requestDTO);
            return new ResponseEntity<>(createdType, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // ========== UPDATE ==========
    @PutMapping("/{id}")
    public ResponseEntity<TypeEvenementDTO> updateTypeEvenement(@PathVariable Long id,
                                                                @RequestBody TypeEvenementRequestDTO requestDTO) {
        try {
            TypeEvenementDTO updatedType = typeEvenementService.updateTypeEvenement(id, requestDTO);
            return ResponseEntity.ok(updatedType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // ========== DELETE ==========
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTypeEvenement(@PathVariable Long id) {
        try {
            typeEvenementService.deleteTypeEvenement(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== GET ALL ==========
    @GetMapping
    public ResponseEntity<List<TypeEvenementDTO>> getAllTypeEvenements() {
        List<TypeEvenementDTO> types = typeEvenementService.getAllTypeEvenements();
        return ResponseEntity.ok(types);
    }

    // ========== GET ACTIVE ==========
    @GetMapping("/active")
    public ResponseEntity<List<TypeEvenementDTO>> getActiveTypeEvenements() {
        List<TypeEvenementDTO> types = typeEvenementService.getActiveTypeEvenements();
        return ResponseEntity.ok(types);
    }

    // ========== GET INACTIVE ==========
    @GetMapping("/inactive")
    public ResponseEntity<List<TypeEvenementDTO>> getInactiveTypeEvenements() {
        List<TypeEvenementDTO> types = typeEvenementService.getInactiveTypeEvenements();
        return ResponseEntity.ok(types);
    }

    // ========== GET BY ID ==========
    @GetMapping("/{id}")
    public ResponseEntity<TypeEvenementDTO> getTypeEvenementById(@PathVariable Long id) {
        try {
            TypeEvenementDTO type = typeEvenementService.getTypeEvenementById(id);
            return ResponseEntity.ok(type);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== SEARCH ==========
    @GetMapping("/search")
    public ResponseEntity<List<TypeEvenementDTO>> searchTypeEvenements(@RequestParam String keyword) {
        List<TypeEvenementDTO> types = typeEvenementService.searchTypeEvenements(keyword);
        return ResponseEntity.ok(types);
    }


    // ========== TOGGLE STATUS ==========
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TypeEvenementDTO> toggleTypeEvenementStatus(@PathVariable Long id) {
        try {
            TypeEvenementDTO type = typeEvenementService.toggleTypeEvenementStatus(id);
            return ResponseEntity.ok(type);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // Add to TypeEvenementController.java
    @PutMapping("/{id}/status")
    public ResponseEntity<TypeEvenementDTO> updateStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> status) {
        try {
            TypeEvenementDTO type = typeEvenementService.updateStatus(id, status.get("actif"));
            return ResponseEntity.ok(type);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}