// src/main/java/com/pfe/webapp/controller/PivotController.java
package com.pfe.webapp.controller;

import com.pfe.webapp.dto.pivot.PivotRequestDTO;
import com.pfe.webapp.dto.pivot.PivotResponseDTO;
import com.pfe.webapp.service.pivot.PivotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pivot")
@CrossOrigin(origins = "*")
public class PivotController {

    @Autowired
    private PivotService pivotService;

    @PostMapping("/data")
    public ResponseEntity<PivotResponseDTO> getPivotData(@RequestBody PivotRequestDTO request) {
        PivotResponseDTO response = pivotService.getPivotData(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fields")
    public ResponseEntity<String[]> getAvailableFields() {
        String[] fields = {
                "missionCode", "methodologie", "status",
                "month", "quarter", "year", "projectName"
        };
        return ResponseEntity.ok(fields);
    }

    @GetMapping("/values")
    public ResponseEntity<String[]> getAvailableValues() {
        String[] values = {
                "totalCost", "projectCount", "avgProgression",
                "budget", "objectifVP"
        };
        return ResponseEntity.ok(values);
    }

    @GetMapping("/aggregators")
    public ResponseEntity<String[]> getAggregators() {
        String[] aggregators = {"sum", "avg", "count", "min", "max"};
        return ResponseEntity.ok(aggregators);
    }
}