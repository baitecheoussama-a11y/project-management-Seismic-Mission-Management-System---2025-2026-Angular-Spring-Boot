package com.pfe.webapp.controller.medical;

import com.pfe.webapp.dto.AntecedentsMedicalDTO;
import com.pfe.webapp.service.medical.AntecedentsMedicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/antecedents-medical")
@CrossOrigin(origins = "http://localhost:4200")
public class AntecedentsMedicalController {

    @Autowired
    private AntecedentsMedicalService antecedentsMedicalService;

    @GetMapping("/etat-medical/{etatMedicalId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AntecedentsMedicalDTO>> getAntecedentsByEtatMedicalId(@PathVariable Long etatMedicalId) {
        return ResponseEntity.ok(antecedentsMedicalService.getAntecedentsByEtatMedicalId(etatMedicalId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AntecedentsMedicalDTO> getAntecedentById(@PathVariable Long id) {
        return ResponseEntity.ok(antecedentsMedicalService.getAntecedentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AntecedentsMedicalDTO> createOrUpdateAntecedent(@RequestBody AntecedentsMedicalDTO antecedentDTO) {
        AntecedentsMedicalDTO created = antecedentsMedicalService.createOrUpdateAntecedent(antecedentDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAntecedent(@PathVariable Long id) {
        antecedentsMedicalService.deleteAntecedent(id);
        return ResponseEntity.noContent().build();
    }
}