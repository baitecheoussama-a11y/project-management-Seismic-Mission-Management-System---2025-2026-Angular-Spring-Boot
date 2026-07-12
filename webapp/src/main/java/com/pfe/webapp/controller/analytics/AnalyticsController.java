// src/main/java/com/pfe/webapp/controller/analytics/AnalyticsController.java
package com.pfe.webapp.controller.analytics;

import com.pfe.webapp.dto.analytics.CostByMissionDTO;
import com.pfe.webapp.dto.analytics.KPIDashboardDTO;
import com.pfe.webapp.dto.analytics.TrendDataDTO;
import com.pfe.webapp.service.analytics.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/kpis")
    public ResponseEntity<KPIDashboardDTO> getKPIs() {
        System.out.println("GET /api/analytics/kpis");
        KPIDashboardDTO kpis = analyticsService.getKPIs();
        return ResponseEntity.ok(kpis);
    }

    @GetMapping("/cost-by-mission")
    public ResponseEntity<List<CostByMissionDTO>> getCostByMission() {
        System.out.println("GET /api/analytics/cost-by-mission");
        List<CostByMissionDTO> costByMission = analyticsService.getCostByMission();
        return ResponseEntity.ok(costByMission);
    }

    @GetMapping("/trends")
    public ResponseEntity<List<TrendDataDTO>> getTrendData(
            @RequestParam(defaultValue = "1") Long missionId,
            @RequestParam(defaultValue = "30") Integer days) {
        System.out.println("GET /api/analytics/trends?missionId=" + missionId + "&days=" + days);
        List<TrendDataDTO> trends = analyticsService.getTrendData(missionId, days);
        return ResponseEntity.ok(trends);
    }
}