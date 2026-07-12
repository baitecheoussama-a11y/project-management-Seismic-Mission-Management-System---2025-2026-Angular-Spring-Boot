package com.pfe.webapp.controller.ressource;

import com.pfe.webapp.entity.ressource.Ressource;
import com.pfe.webapp.service.RessourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ressources")
@CrossOrigin(origins = "http://localhost:4200")
public class RessourceController {

    @Autowired
    private RessourceService ressourceService;

    @GetMapping
    public ResponseEntity<List<Ressource>> getAll() {
        return ResponseEntity.ok(ressourceService.getAllRessources());
    }

    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<Ressource>> getByCategorie(@PathVariable Long categorieId) {
        return ResponseEntity.ok(ressourceService.getRessourcesByCategory(categorieId));
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<Ressource>> getByType(@PathVariable Long typeId) {
        return ResponseEntity.ok(ressourceService.getRessourcesByType(typeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ressource> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ressourceService.getRessourceById(id));
    }

    @PostMapping
    public ResponseEntity<Ressource> create(@RequestBody Map<String, Object> payload) {
        Ressource ressource = new Ressource();
        ressource.setTitre((String) payload.get("titre"));
        ressource.setDescription((String) payload.get("description"));
        ressource.setQuantite(Double.valueOf(payload.get("quantite").toString()));
        ressource.setUnite((String) payload.get("unite"));
        ressource.setCout(Double.valueOf(payload.get("cout").toString()));
        if (payload.get("dateAchat") != null) {
            ressource.setDateAchat(java.time.LocalDate.parse(payload.get("dateAchat").toString()));
        }
        Long typeId = Long.valueOf(payload.get("typeRessourceId").toString());
        return new ResponseEntity<>(ressourceService.createRessource(ressource, typeId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ressource> update(@PathVariable Long id, @RequestBody Ressource ressource) {
        return ResponseEntity.ok(ressourceService.updateRessource(id, ressource));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ressourceService.deleteRessource(id);
        return ResponseEntity.noContent().build();
    }
}