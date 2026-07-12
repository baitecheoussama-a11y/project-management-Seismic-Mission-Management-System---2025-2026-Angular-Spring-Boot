package com.pfe.webapp.dto;

import java.time.LocalDate;

public class UpdatePanneRequestDTO {
    private LocalDate datePanne;
    private Integer quantity;
    private String detailProbleme;

    // Getters and Setters
    public LocalDate getDatePanne() { return datePanne; }
    public void setDatePanne(LocalDate datePanne) { this.datePanne = datePanne; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getDetailProbleme() { return detailProbleme; }
    public void setDetailProbleme(String detailProbleme) { this.detailProbleme = detailProbleme; }
}