package com.pfe.webapp.controller.medical;

import com.pfe.webapp.dto.EtatMedicalDTO;
import com.pfe.webapp.dto.EtatMedicalResponseDTO;
import com.pfe.webapp.service.medical.EtatMedicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/etat-medical")
@CrossOrigin(origins = "http://localhost:4200")
public class EtatMedicalController {

    @Autowired
    private EtatMedicalService etatMedicalService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<EtatMedicalResponseDTO>> getAllEtatMedicals() {
        return ResponseEntity.ok(etatMedicalService.getAllEtatMedicals());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EtatMedicalResponseDTO> getEtatMedicalById(@PathVariable Long id) {
        return ResponseEntity.ok(etatMedicalService.getEtatMedicalById(id));
    }

    @GetMapping("/employe/{employeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EtatMedicalResponseDTO> getEtatMedicalByEmployeId(@PathVariable Long employeId) {
        return ResponseEntity.ok(etatMedicalService.getEtatMedicalByEmployeId(employeId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<EtatMedicalResponseDTO> createOrUpdateEtatMedical(@RequestBody EtatMedicalDTO etatMedicalDTO) {
        EtatMedicalResponseDTO created = etatMedicalService.createOrUpdateEtatMedical(etatMedicalDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteEtatMedical(@PathVariable Long id) {
        etatMedicalService.deleteEtatMedical(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employe/{employeId}/exists")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> existsByEmployeId(@PathVariable Long employeId) {
        return ResponseEntity.ok(etatMedicalService.existsByEmployeId(employeId));
    }
}