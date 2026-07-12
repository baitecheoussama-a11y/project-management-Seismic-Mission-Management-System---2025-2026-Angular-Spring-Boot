// controller/ressource/ConsommationController.java
package com.pfe.webapp.controller.ressource;

import com.pfe.webapp.dto.ConsumptionDetailDTO;
import com.pfe.webapp.dto.ConsumptionRequestDTO;
import com.pfe.webapp.dto.MissionResourceSummaryDTO;
import com.pfe.webapp.entity.ressource.Consommation;
import com.pfe.webapp.service.ConsommationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consommations")
@CrossOrigin(origins = "http://localhost:4200")
public class ConsommationController {

    @Autowired
    private ConsommationService consommationService;

    @GetMapping
    public ResponseEntity<List<Consommation>> getAll() {
        return ResponseEntity.ok(consommationService.getAllConsommations());
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<Consommation>> getByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(consommationService.getByMissionId(missionId));
    }

    @GetMapping("/mission/{missionId}/summary")
    public ResponseEntity<MissionResourceSummaryDTO> getMissionResourceSummary(@PathVariable Long missionId) {
        return ResponseEntity.ok(consommationService.getMissionResourceSummary(missionId));
    }

    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<List<Consommation>> getByResource(@PathVariable Long resourceId) {
        return ResponseEntity.ok(consommationService.getByRessourceId(resourceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consommation> getById(@PathVariable Long id) {
        return ResponseEntity.ok(consommationService.getConsommationById(id));
    }

    @PostMapping
    public ResponseEntity<Consommation> create(@RequestBody ConsumptionRequestDTO request) {
        return new ResponseEntity<>(consommationService.createConsommation(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consommation> update(@PathVariable Long id, @RequestBody Consommation consommation) {
        return ResponseEntity.ok(consommationService.updateConsommation(id, consommation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        consommationService.deleteConsommation(id);
        return ResponseEntity.noContent().build();
    }
}