// dto/RapportDetailsRequestDTO.java
package com.pfe.webapp.dto.rapport;

import java.util.Map;

public class RapportDetailsRequestDTO {
    private Long rapportId;
    private Map<String, Object> details;

    public Long getRapportId() { return rapportId; }
    public void setRapportId(Long rapportId) { this.rapportId = rapportId; }

    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}