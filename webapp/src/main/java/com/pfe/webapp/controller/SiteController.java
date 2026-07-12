// controller/SiteController.java
package com.pfe.webapp.controller;

import com.pfe.webapp.dto.SiteRequestDTO;
import com.pfe.webapp.dto.SiteResponseDTO;
import com.pfe.webapp.dto.WilayaDTO;
import com.pfe.webapp.service.SiteService;
import com.pfe.webapp.service.WilayaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@CrossOrigin(origins = "http://localhost:4200")
public class SiteController {

    @Autowired
    private SiteService siteService;

    @Autowired
    private WilayaService wilayaService;


    @GetMapping("/project/{projectId}")
    public ResponseEntity<SiteResponseDTO> getSiteByProjectId(@PathVariable Long projectId) {
        SiteResponseDTO site = siteService.getSiteByProjectId(projectId);
        if (site == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(site);
    }

    @PostMapping
    public ResponseEntity<SiteResponseDTO> createOrUpdateSite(@RequestBody SiteRequestDTO request) {
        SiteResponseDTO site = siteService.createOrUpdateSite(request);
        return new ResponseEntity<>(site, HttpStatus.CREATED);
    }

    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long projectId) {
        siteService.deleteSite(projectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/wilayas")
    public ResponseEntity<List<WilayaDTO>> getAllWilayas() {
        return ResponseEntity.ok(wilayaService.getAllWilayas());
    }
}