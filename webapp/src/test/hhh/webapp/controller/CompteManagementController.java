package com.pfe.webapp.controller;

import com.pfe.webapp.dto.CompteDTO;
import com.pfe.webapp.dto.CompteResponseDTO;
import com.pfe.webapp.entity.StatusCompte;
import com.pfe.webapp.service.CompteManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comptes")
@CrossOrigin(origins = "http://localhost:4200")
public class CompteManagementController {

    @Autowired
    private CompteManagementService compteManagementService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<CompteResponseDTO>> getAllComptes() {
        return ResponseEntity.ok(compteManagementService.getAllComptes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CompteResponseDTO> getCompteById(@PathVariable Long id) {
        return ResponseEntity.ok(compteManagementService.getCompteById(id));
    }

    // ✅ أضف هذا الـ endpoint الجديد
    @GetMapping("/employe/{employeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CompteResponseDTO> getCompteByEmployeId(@PathVariable Long employeId) {
        CompteResponseDTO compte = compteManagementService.getCompteByEmployeId(employeId);
        if (compte == null) {
            return ResponseEntity.ok(null);  // 200 مع null (ليس خطأ، فقط لا يوجد حساب)
        }
        return ResponseEntity.ok(compte);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CompteResponseDTO> createCompte(@RequestBody CompteDTO compteDTO) {
        CompteResponseDTO created = compteManagementService.createCompte(compteDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CompteResponseDTO> updateStatus(@PathVariable Long id,
                                                          @RequestParam StatusCompte status) {
        return ResponseEntity.ok(compteManagementService.updateCompteStatus(id, status));
    }

    @PutMapping("/{id}/username")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CompteResponseDTO> updateUsername(@PathVariable Long id,
                                                            @RequestParam String username) {
        return ResponseEntity.ok(compteManagementService.updateUsername(id, username));
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id) {
        compteManagementService.resetPassword(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCompte(@PathVariable Long id) {
        compteManagementService.deleteCompte(id);
        return ResponseEntity.noContent().build();
    }
}