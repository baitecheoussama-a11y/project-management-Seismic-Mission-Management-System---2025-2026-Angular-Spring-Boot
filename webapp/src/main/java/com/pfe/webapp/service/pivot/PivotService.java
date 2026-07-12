// src/main/java/com/pfe/webapp/service/pivot/PivotService.java
package com.pfe.webapp.service.pivot;

import com.pfe.webapp.dto.pivot.PivotRequestDTO;
import com.pfe.webapp.dto.pivot.PivotResponseDTO;

public interface PivotService {
    PivotResponseDTO getPivotData(PivotRequestDTO request);
}