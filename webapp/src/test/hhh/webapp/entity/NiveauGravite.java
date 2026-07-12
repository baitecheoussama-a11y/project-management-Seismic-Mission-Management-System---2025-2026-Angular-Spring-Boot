package com.pfe.webapp.entity;

public enum NiveauGravite {
    CRITIQUE("Critique", "#ef4444"),
    ELEVE("Élevé", "#f97316"),
    MOYEN("Moyen", "#f59e0b"),
    FAIBLE("Faible", "#10b981");

    private final String label;
    private final String color;

    NiveauGravite(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() { return label; }
    public String getColor() { return color; }
}