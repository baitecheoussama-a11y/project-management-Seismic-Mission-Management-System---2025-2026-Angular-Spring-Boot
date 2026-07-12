package com.pfe.webapp.entity;

public enum NiveauPriorite {
    ELEVEE("Élevée", "#ef4444", "HIGH"),
    MOYENNE("Moyenne", "#f59e0b", "MEDIUM"),
    FAIBLE("Faible", "#10b981", "LOW");

    private final String label;
    private final String color;
    private final String code;

    NiveauPriorite(String label, String color, String code) {
        this.label = label;
        this.color = color;
        this.code = code;
    }

    public String getLabel() { return label; }
    public String getColor() { return color; }
    public String getCode() { return code; }
}