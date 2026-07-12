package com.pfe.webapp.controller.ressource;

import com.pfe.webapp.entity.ressource.CategorieRessource;
import com.pfe.webapp.service.RessourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ressources/categories")
@CrossOrigin(origins = "http://localhost:4200")
public class CategorieRessourceController {

    @Autowired
    private RessourceService ressourceService;

    @GetMapping
    public ResponseEntity<List<CategorieRessource>> getAll() {
        return ResponseEntity.ok(ressourceService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieRessource> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ressourceService.getCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<CategorieRessource> create(@RequestBody CategorieRessource category) {
        return new ResponseEntity<>(ressourceService.createCategory(category), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategorieRessource> update(@PathVariable Long id, @RequestBody CategorieRessource category) {
        return ResponseEntity.ok(ressourceService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ressourceService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}