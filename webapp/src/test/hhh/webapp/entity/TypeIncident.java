package com.pfe.webapp.entity;

public enum TypeIncident {
    ACCIDENT_TRAVAIL("Accident de travail", "#ef4444"),
    MALADIE_PROFESSIONNELLE("Maladie professionnelle", "#f97316"),
    INCIDENT_SECURITE("Incident de sécurité", "#f59e0b"),
    INCIDENT_ENVIRONNEMENTAL("Incident environnemental", "#10b981"),
    AUTRE("Autre", "#6b7280");

    private final String label;
    private final String color;

    TypeIncident(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }
}