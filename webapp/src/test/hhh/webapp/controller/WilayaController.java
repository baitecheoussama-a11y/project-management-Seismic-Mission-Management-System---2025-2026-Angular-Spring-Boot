// controller/WilayaController.java
package com.pfe.webapp.controller;

import com.pfe.webapp.dto.WilayaDTO;
import com.pfe.webapp.service.WilayaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wilayas")
@CrossOrigin(origins = "http://localhost:4200")
public class WilayaController {

    @Autowired
    private WilayaService wilayaService;

    // Get all wilayas (returns DTOs)
    @GetMapping
    public ResponseEntity<List<WilayaDTO>> getAllWilayas() {
        return ResponseEntity.ok(wilayaService.getAllWilayas());
    }

    // Get wilaya by ID (returns DTO)
    @GetMapping("/{numWilaya}")
    public ResponseEntity<WilayaDTO> getWilayaById(@PathVariable Integer numWilaya) {
        WilayaDTO wilaya = wilayaService.getWilayaById(numWilaya);
        return ResponseEntity.ok(wilaya);
    }

    // Get wilaya by name (returns DTO)
    @GetMapping("/nom/{nom}")
    public ResponseEntity<WilayaDTO> getWilayaByNom(@PathVariable String nom) {
        WilayaDTO wilaya = wilayaService.getWilayaByNom(nom);
        return ResponseEntity.ok(wilaya);
    }

    // Get wilaya with full entity (for admin use)
    @GetMapping("/{numWilaya}/entity")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATEUR')")
    public ResponseEntity<com.pfe.webapp.entity.Wilaya> getWilayaEntityById(@PathVariable Integer numWilaya) {
        return ResponseEntity.ok(wilayaService.getWilayaEntityById(numWilaya));
    }

    // Create new wilaya (admin only)
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRATEUR')")
    public ResponseEntity<WilayaDTO> createWilaya(@RequestBody WilayaDTO wilayaDTO) {
        WilayaDTO created = wilayaService.createWilaya(wilayaDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Update existing wilaya (admin only)
    @PutMapping("/{numWilaya}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATEUR')")
    public ResponseEntity<WilayaDTO> updateWilaya(@PathVariable Integer numWilaya, @RequestBody WilayaDTO wilayaDTO) {
        WilayaDTO updated = wilayaService.updateWilaya(numWilaya, wilayaDTO);
        return ResponseEntity.ok(updated);
    }

    // Delete wilaya (admin only)
    @DeleteMapping("/{numWilaya}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATEUR')")
    public ResponseEntity<Void> deleteWilaya(@PathVariable Integer numWilaya) {
        wilayaService.deleteWilaya(numWilaya);
        return ResponseEntity.noContent().build();
    }
}