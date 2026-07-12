package com.pfe.webapp.controller;

import com.pfe.webapp.dto.AffectationRoleDTO;
import com.pfe.webapp.service.AffectationRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/affectations-roles")
@CrossOrigin(origins = "http://localhost:4200")
public class AffectationRoleController {

    @Autowired
    private AffectationRoleService affectationRoleService;

    @GetMapping("/compte/{compteId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AffectationRoleDTO>> getRolesByCompte(@PathVariable Long compteId) {
        return ResponseEntity.ok(affectationRoleService.getRolesByCompteId(compteId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AffectationRoleDTO> create(@RequestBody AffectationRoleDTO dto) {
        return new ResponseEntity<>(affectationRoleService.createAffectation(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AffectationRoleDTO> update(@PathVariable Long id, @RequestBody AffectationRoleDTO dto) {
        return ResponseEntity.ok(affectationRoleService.updateAffectation(id, dto));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AffectationRoleDTO> toggleActive(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(affectationRoleService.toggleActive(id, active));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        affectationRoleService.deleteAffectation(id);
        return ResponseEntity.noContent().build();
    }
}