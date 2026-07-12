// controller/ActiveController.java
package com.pfe.webapp.controller.team;

import com.pfe.webapp.dto.team.ActiveSimpleDTO;
import com.pfe.webapp.dto.team.ActiveRequestDTO;
import com.pfe.webapp.service.team.ActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actives")
@CrossOrigin(origins = "http://localhost:4200")
public class ActiveController {

    @Autowired
    private ActiveService activeService;

    @GetMapping
    public ResponseEntity<List<ActiveSimpleDTO>> getAllActives() {
        return ResponseEntity.ok(activeService.getAllActives());
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<ActiveSimpleDTO>> getActivesByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(activeService.getActivesByMission(missionId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ActiveSimpleDTO>> getAvailableActives(@RequestParam Long missionId) {
        return ResponseEntity.ok(activeService.getAvailableActives(missionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActiveSimpleDTO> getActiveById(@PathVariable Long id) {
        return ResponseEntity.ok(activeService.getActiveById(id));
    }

    @PostMapping
    public ResponseEntity<ActiveSimpleDTO> createActive(@RequestBody ActiveRequestDTO request) {
        ActiveSimpleDTO created = activeService.createActive(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActiveSimpleDTO> updateActive(@PathVariable Long id, @RequestBody ActiveRequestDTO request) {
        ActiveSimpleDTO updated = activeService.updateActive(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActive(@PathVariable Long id) {
        activeService.deleteActive(id);
        return ResponseEntity.noContent().build();
    }
}