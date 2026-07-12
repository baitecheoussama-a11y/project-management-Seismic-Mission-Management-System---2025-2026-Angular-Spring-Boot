package com.pfe.webapp.dto.av;

import com.pfe.webapp.entity.StatusEtatAvancement;

public class UpdateStatusRequestDTO {
    private StatusEtatAvancement status;

    // Getters and Setters
    public StatusEtatAvancement getStatus() { return status; }
    public void setStatus(StatusEtatAvancement status) { this.status = status; }
}