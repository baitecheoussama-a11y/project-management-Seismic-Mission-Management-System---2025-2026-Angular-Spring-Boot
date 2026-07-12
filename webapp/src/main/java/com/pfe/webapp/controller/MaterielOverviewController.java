// MaterielOverviewController.java
package com.pfe.webapp.controller;
import com.pfe.webapp.dto.MaterielDetailDTO;
import com.pfe.webapp.dto.MaterielOverviewDTO;
import com.pfe.webapp.service.MaterielOverviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/materiels")
@CrossOrigin(origins = "http://localhost:4200")
public class MaterielOverviewController {

    @Autowired
    private MaterielOverviewService overviewService;

    @GetMapping("/{id}/overview")
    public ResponseEntity<MaterielOverviewDTO> getMaterielOverview(@PathVariable Long id) {
        MaterielOverviewDTO overview = overviewService.getMaterielOverview(id);
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<MaterielDetailDTO> getMaterielDetails(@PathVariable Long id) {
        MaterielDetailDTO details = overviewService.getMaterielDetails(id);
        return ResponseEntity.ok(details);
    }
}