// controller/EquipeDetailController.java
package com.pfe.webapp.controller.team;

import com.pfe.webapp.dto.team.EquipeDetailDTO;
import com.pfe.webapp.service.team.EquipeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipe-detail")
@CrossOrigin(origins = "http://localhost:4200")
public class EquipeDetailController {

    @Autowired
    private EquipeDetailService equipeDetailService;

    @GetMapping("/{equipeId}")
    public ResponseEntity<EquipeDetailDTO> getEquipeDetail(
            @PathVariable Long equipeId,
            @RequestParam Long missionId) {
        EquipeDetailDTO detail = equipeDetailService.getEquipeDetail(equipeId, missionId);
        return ResponseEntity.ok(detail);
    }
}