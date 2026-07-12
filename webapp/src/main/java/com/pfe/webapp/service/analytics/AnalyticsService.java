// src/main/java/com/pfe/webapp/service/analytics/AnalyticsService.java
package com.pfe.webapp.service.analytics;

import com.pfe.webapp.dto.analytics.CostByMissionDTO;
import com.pfe.webapp.dto.analytics.KPIDashboardDTO;
import com.pfe.webapp.dto.analytics.TrendDataDTO;

import java.util.List;

public interface AnalyticsService {
    KPIDashboardDTO getKPIs();
    List<CostByMissionDTO> getCostByMission();
    List<TrendDataDTO> getTrendData(Long missionId, Integer days);
}