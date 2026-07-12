// controller/ressource/ContexteController.java
package com.pfe.webapp.controller.ressource;

import com.pfe.webapp.entity.ressource.Contexte;
import com.pfe.webapp.service.ContexteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contextes")
@CrossOrigin(origins = "http://localhost:4200")
public class ContexteController {

    @Autowired
    private ContexteService contexteService;

    @GetMapping
    public ResponseEntity<List<Contexte>> getAll() {
        return ResponseEntity.ok(contexteService.getAllContextes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contexte> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contexteService.getContexteById(id));
    }

    @PostMapping
    public ResponseEntity<Contexte> create(@RequestBody Contexte contexte) {
        return new ResponseEntity<>(contexteService.createContexte(contexte), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contexte> update(@PathVariable Long id, @RequestBody Contexte contexte) {
        return ResponseEntity.ok(contexteService.updateContexte(id, contexte));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contexteService.deleteContexte(id);
        return ResponseEntity.noContent().build();
    }
}