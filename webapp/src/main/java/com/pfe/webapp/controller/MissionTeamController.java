// controller/MissionTeamController.java
package com.pfe.webapp.controller;

import com.pfe.webapp.dto.AffectationRequestDTO;
import com.pfe.webapp.dto.EmployeDTO;
import com.pfe.webapp.dto.EquipeDTO;
import com.pfe.webapp.dto.MissionTeamDTO;
import com.pfe.webapp.service.MissionTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mission-team")
@CrossOrigin(origins = "http://localhost:4200")
public class MissionTeamController {

    @Autowired
    private MissionTeamService missionTeamService;

    @GetMapping("/available-employees")
    public ResponseEntity<List<EmployeDTO>> getAvailableEmployees() {
        return ResponseEntity.ok(missionTeamService.getAvailableEmployees());
    }

    @GetMapping("/all-employees")
    public ResponseEntity<List<EmployeDTO>> getAllEmployeesWithStatus() {
        return ResponseEntity.ok(missionTeamService.getAllEmployeesWithStatus());
    }

    @GetMapping("/mission/{missionId}")
    public ResponseEntity<MissionTeamDTO> getMissionTeam(@PathVariable Long missionId) {
        return ResponseEntity.ok(missionTeamService.getMissionTeam(missionId));
    }

    @GetMapping("/equipes")
    public ResponseEntity<List<EquipeDTO>> getAllEquipes() {
        return ResponseEntity.ok(missionTeamService.getAllEquipes());
    }

    @PostMapping("/add-members")
    public ResponseEntity<Void> addEmployeesToMission(@RequestBody AffectationRequestDTO request) {
        missionTeamService.addEmployeesToMission(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{missionId}/employee/{employeId}/equipe/{equipeId}")
    public ResponseEntity<Void> updateEmployeeTeam(@PathVariable Long missionId,
                                                   @PathVariable Long employeId,
                                                   @PathVariable Long equipeId) {
        missionTeamService.updateEmployeeTeam(missionId, employeId, equipeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{missionId}/employee/{employeId}")
    public ResponseEntity<Void> removeEmployeeFromMission(@PathVariable Long missionId,
                                                          @PathVariable Long employeId) {
        missionTeamService.removeEmployeeFromMission(missionId, employeId);
        return ResponseEntity.noContent().build();
    }
}