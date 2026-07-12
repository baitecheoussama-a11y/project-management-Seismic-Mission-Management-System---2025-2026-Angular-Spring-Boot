package com.pfe.webapp.dto;

public class UpdateInternalRepairRequestDTO {
    private String technicien;
    private Integer quantity;
    private String detailProbleme;

    // Getters and Setters
    public String getTechnicien() { return technicien; }
    public void setTechnicien(String technicien) { this.technicien = technicien; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getDetailProbleme() { return detailProbleme; }
    public void setDetailProbleme(String detailProbleme) { this.detailProbleme = detailProbleme; }
}