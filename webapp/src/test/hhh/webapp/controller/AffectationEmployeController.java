package com.pfe.webapp.controller;

import com.pfe.webapp.entity.AffectationEmploye;
import com.pfe.webapp.service.AffectationEmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/affectations")
@CrossOrigin(origins = "http://localhost:4200")
public class AffectationEmployeController {

    @Autowired
    private AffectationEmployeService affectationEmployeService;  // ✅ changed from 'service' to 'affectationEmployeService'

    @GetMapping
    public List<AffectationEmploye> getAll() {
        return affectationEmployeService.getAllAffectations();  // ✅ fixed method name
    }

    @GetMapping("/{id}")
    public AffectationEmploye getById(@PathVariable Long id) {
        return affectationEmployeService.getAffectationById(id);  // ✅ added missing GET by ID
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATEUR', 'ROLE_CHEF_MISSION')")
    public AffectationEmploye create(@RequestBody AffectationEmploye affectation) {
        return affectationEmployeService.createAffectation(affectation);  // ✅ fixed method name
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMINISTRATEUR', 'ROLE_CHEF_MISSION')")
    public AffectationEmploye update(@PathVariable Long id, @RequestBody AffectationEmploye affectation) {
        return affectationEmployeService.updateAffectation(id, affectation);  // ✅ added missing PUT
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATEUR')")
    public void delete(@PathVariable Long id) {
        affectationEmployeService.deleteAffectation(id);  // ✅ fixed method name
    }

    @GetMapping("/employe/{employeId}")
    public List<AffectationEmploye> getByEmploye(@PathVariable Long employeId) {
        return affectationEmployeService.getAffectationsByEmploye(employeId);  // ✅ added useful endpoint
    }

    @GetMapping("/mission/{missionId}")
    public List<AffectationEmploye> getByMission(@PathVariable Long missionId) {
        return affectationEmployeService.getAffectationsByMission(missionId);  // ✅ added useful endpoint
    }
}