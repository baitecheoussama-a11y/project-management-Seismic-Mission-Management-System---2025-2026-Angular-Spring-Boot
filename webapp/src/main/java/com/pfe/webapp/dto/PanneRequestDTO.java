package com.pfe.webapp.dto;

import java.time.LocalDate;

public class PanneRequestDTO {
    private Long materielId;
    private LocalDate datePanne;
    private Integer quantity;
    private String detailProbleme;
    private Long missionId;      // ✅ Optional: إذا كان العطل من مهمة محددة
    private Long affectationId;  // ✅ Optional: إذا كان من تعيين محدد

    // Getters and Setters
    public Long getMaterielId() { return materielId; }
    public void setMaterielId(Long materielId) { this.materielId = materielId; }

    public LocalDate getDatePanne() { return datePanne; }
    public void setDatePanne(LocalDate datePanne) { this.datePanne = datePanne; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getDetailProbleme() { return detailProbleme; }
    public void setDetailProbleme(String detailProbleme) { this.detailProbleme = detailProbleme; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public Long getAffectationId() { return affectationId; }
    public void setAffectationId(Long affectationId) { this.affectationId = affectationId; }
}