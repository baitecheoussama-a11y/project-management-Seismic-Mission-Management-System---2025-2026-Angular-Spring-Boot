package com.pfe.webapp.entity;

public enum StatusPointage {
    PRESENT("Present", "#10b981"),
    ABSENT("Absent", "#ef4444"),
    RETARD("Late", "#f59e0b"),
    CONGE("Leave", "#3b82f6"),
    MISSION("Mission", "#8b5cf6");

    private final String label;
    private final String color;

    StatusPointage(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }

    // ✅ New: Get status from string
    public static StatusPointage fromString(String value) {
        for (StatusPointage status : StatusPointage.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return StatusPointage.PRESENT;
    }
}