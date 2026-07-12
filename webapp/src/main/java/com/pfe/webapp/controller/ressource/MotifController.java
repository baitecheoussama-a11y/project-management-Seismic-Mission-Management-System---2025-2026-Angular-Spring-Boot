// controller/ressource/MotifController.java
package com.pfe.webapp.controller.ressource;

import com.pfe.webapp.entity.ressource.Motif;
import com.pfe.webapp.service.MotifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/motifs")
@CrossOrigin(origins = "http://localhost:4200")
public class MotifController {

    @Autowired
    private MotifService motifService;

    @GetMapping
    public ResponseEntity<List<Motif>> getAll() {
        return ResponseEntity.ok(motifService.getAllMotifs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Motif> getById(@PathVariable Long id) {
        return ResponseEntity.ok(motifService.getMotifById(id));
    }

    @PostMapping
    public ResponseEntity<Motif> create(@RequestBody Motif motif) {
        return new ResponseEntity<>(motifService.createMotif(motif), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Motif> update(@PathVariable Long id, @RequestBody Motif motif) {
        return ResponseEntity.ok(motifService.updateMotif(id, motif));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        motifService.deleteMotif(id);
        return ResponseEntity.noContent().build();
    }
}