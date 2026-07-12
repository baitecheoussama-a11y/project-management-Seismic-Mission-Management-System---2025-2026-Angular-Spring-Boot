package com.pfe.webapp.controller;

import com.pfe.webapp.entity.Coordonnee;
import com.pfe.webapp.service.CoordonneeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordonnees")
@CrossOrigin(origins = "http://localhost:4200")
public class CoordonneeController {

    @Autowired
    private CoordonneeService coordonneeService;

    @GetMapping
    public List<Coordonnee> getAllCoordonnees() {
        return coordonneeService.getAllCoordonnees();
    }

    @GetMapping("/{id}")
    public Coordonnee getCoordonneeById(@PathVariable Long id) {
        return coordonneeService.getCoordonneeById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATEUR', 'ROLE_CHEF_MISSION')")
    public Coordonnee createCoordonnee(@RequestBody Coordonnee coordonnee) {
        return coordonneeService.createCoordonnee(coordonnee);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATEUR', 'ROLE_CHEF_MISSION')")
    public Coordonnee updateCoordonnee(@PathVariable Long id, @RequestBody Coordonnee coordonnee) {
        return coordonneeService.updateCoordonnee(id, coordonnee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATEUR')")
    public void deleteCoordonnee(@PathVariable Long id) {
        coordonneeService.deleteCoordonnee(id);
    }
}