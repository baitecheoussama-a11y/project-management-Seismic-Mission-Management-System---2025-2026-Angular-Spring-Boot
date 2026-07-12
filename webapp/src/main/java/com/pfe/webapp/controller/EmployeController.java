package com.pfe.webapp.controller;

import com.pfe.webapp.dto.EmployeAccountDetailsDTO;
import com.pfe.webapp.dto.EmployeDTO;
import com.pfe.webapp.dto.EmployeResponseDTO;
import com.pfe.webapp.service.EmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employes")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeController {

    @Autowired
    private EmployeService employeService;

    @GetMapping

    public ResponseEntity<List<EmployeResponseDTO>> getAllEmployes() {
        return ResponseEntity.ok(employeService.getAllEmployes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeResponseDTO> getEmployeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeService.getEmployeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeResponseDTO> createEmploye(@RequestBody EmployeDTO employeDTO) {
        EmployeResponseDTO created = employeService.createEmploye(employeDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeResponseDTO> updateEmploye(@PathVariable Long id,
                                                            @RequestBody EmployeDTO employeDTO) {
        return ResponseEntity.ok(employeService.updateEmploye(id, employeDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmploye(@PathVariable Long id) {
        employeService.deleteEmploye(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/account-details")
    @PreAuthorize("isAuthenticated()")  // أي مستخدم مسجل دخول
    public ResponseEntity<EmployeAccountDetailsDTO> getEmployeAccountDetails(@PathVariable Long id) {
        return ResponseEntity.ok(employeService.getEmployeAccountDetails(id));
    }

// Add these endpoints to your existing EmployeController

    @PutMapping("/{employeId}/assign-fonction/{fonctionId}")
    public ResponseEntity<EmployeDTO> assignFonctionToEmploye(
            @PathVariable Long employeId,
            @PathVariable Long fonctionId) {
        EmployeDTO updatedEmploye = employeService.assignFonctionToEmploye(employeId, fonctionId);
        return ResponseEntity.ok(updatedEmploye);
    }

    @DeleteMapping("/{employeId}/remove-fonction")
    public ResponseEntity<EmployeDTO> removeFonctionFromEmploye(@PathVariable Long employeId) {
        EmployeDTO updatedEmploye = employeService.removeFonctionFromEmploye(employeId);
        return ResponseEntity.ok(updatedEmploye);
    }

    // EmployeController.java - Add this endpoint
    @GetMapping("/by-fonction/{fonctionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeResponseDTO>> getEmployesByFonction(@PathVariable Long fonctionId) {
        return ResponseEntity.ok(employeService.getEmployesByFonction(fonctionId));
    }
}