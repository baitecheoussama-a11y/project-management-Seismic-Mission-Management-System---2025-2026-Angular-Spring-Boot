package com.pfe.webapp.controller.ressource;

import com.pfe.webapp.entity.ressource.TypeRessource;
import com.pfe.webapp.service.RessourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ressources/types")
@CrossOrigin(origins = "http://localhost:4200")
public class TypeRessourceController {

    @Autowired
    private RessourceService ressourceService;

    @GetMapping
    public ResponseEntity<List<TypeRessource>> getAll() {
        return ResponseEntity.ok(ressourceService.getAllTypes());
    }

    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<TypeRessource>> getByCategorie(@PathVariable Long categorieId) {
        return ResponseEntity.ok(ressourceService.getTypesByCategory(categorieId));
    }

    @PostMapping
    public ResponseEntity<TypeRessource> create(@RequestBody Map<String, Object> payload) {
        String nom = (String) payload.get("nom");
        Long categorieId = Long.valueOf(payload.get("categorieId").toString());
        TypeRessource type = new TypeRessource();
        type.setNom(nom);
        return new ResponseEntity<>(ressourceService.createType(type, categorieId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeRessource> update(@PathVariable Long id, @RequestBody TypeRessource type) {
        return ResponseEntity.ok(ressourceService.updateType(id, type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ressourceService.deleteType(id);
        return ResponseEntity.noContent().build();
    }
}