package com.pfe.webapp.controller;

import com.pfe.webapp.dto.ContratDTO;
import com.pfe.webapp.dto.ContratResponseDTO;
import com.pfe.webapp.service.ContratService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contrats")
@CrossOrigin(origins = "http://localhost:4200")
public class ContratController {

    @Autowired
    private ContratService contratService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ContratResponseDTO>> getAllContrats() {
        return ResponseEntity.ok(contratService.getAllContrats());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ContratResponseDTO> getContratById(@PathVariable Long id) {
        return ResponseEntity.ok(contratService.getContratById(id));
    }

    @GetMapping("/employe/{employeId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ContratResponseDTO>> getContratsByEmploye(@PathVariable Long employeId) {
        return ResponseEntity.ok(contratService.getContratsByEmploye(employeId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ContratResponseDTO> createContrat(@RequestBody ContratDTO contratDTO) {
        ContratResponseDTO created = contratService.createContrat(contratDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ContratResponseDTO> updateContrat(@PathVariable Long id,
                                                            @RequestBody ContratDTO contratDTO) {
        return ResponseEntity.ok(contratService.updateContrat(id, contratDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteContrat(@PathVariable Long id) {
        contratService.deleteContrat(id);
        return ResponseEntity.noContent().build();
    }
}