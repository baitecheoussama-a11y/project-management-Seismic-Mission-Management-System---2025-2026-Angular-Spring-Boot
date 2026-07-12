// enums/StatusEtatAvancement.java
package com.pfe.webapp.entity;

public enum StatusEtatAvancement {
    PLANIFIER("Planifié"),
    ENCOURS("En cours"),
    ENATTENTE("En attente"),
    ENRETARD("En retard"),
    TERMINI("Terminé"),
    ANNULE("1");

    private final String label;

    StatusEtatAvancement(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static StatusEtatAvancement fromLabel(String label) {
        for (StatusEtatAvancement status : values()) {
            if (status.label.equals(label)) {
                return status;
            }
        }
        return null;
    }
}