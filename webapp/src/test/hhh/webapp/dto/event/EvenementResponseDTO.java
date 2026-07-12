package com.pfe.webapp.dto.event;

import java.time.LocalDate;
import java.time.LocalTime;

public class EvenementResponseDTO {

    private Long id;
    private String titre;
    private String description;
    private LocalDate date;
    private LocalTime heure;
    private String formattedDateTime;
    private Long missionId;
    private String missionNom;
    private Long typeEvenementId;
    private String typeEvenementNom;
    private String niveauPriorite;
    private String niveauPrioriteLabel;
    private String niveauPrioriteColor;
    private boolean isUpcoming;
    private boolean isToday;
    private boolean isPast;

    // Constructors
    public EvenementResponseDTO() {}

    public EvenementResponseDTO(Long id, String titre, String description, LocalDate date,
                                LocalTime heure, Long missionId, String missionNom,
                                Long typeEvenementId, String typeEvenementNom,
                                String niveauPriorite, String niveauPrioriteLabel,
                                String niveauPrioriteColor) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.heure = heure;
        this.missionId = missionId;
        this.missionNom = missionNom;
        this.typeEvenementId = typeEvenementId;
        this.typeEvenementNom = typeEvenementNom;
        this.niveauPriorite = niveauPriorite;
        this.niveauPrioriteLabel = niveauPrioriteLabel;
        this.niveauPrioriteColor = niveauPrioriteColor;

        // Format datetime
        if (date != null && heure != null) {
            this.formattedDateTime = date.toString() + " " + heure.toString();
        } else if (date != null) {
            this.formattedDateTime = date.toString();
        }

        // Set status
        LocalDate today = LocalDate.now();
        this.isUpcoming = date != null && date.isAfter(today);
        this.isToday = date != null && date.equals(today);
        this.isPast = date != null && date.isBefore(today);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public String getFormattedDateTime() {
        return formattedDateTime;
    }

    public void setFormattedDateTime(String formattedDateTime) {
        this.formattedDateTime = formattedDateTime;
    }

    public Long getMissionId() {
        return missionId;
    }

    public void setMissionId(Long missionId) {
        this.missionId = missionId;
    }

    public String getMissionNom() {
        return missionNom;
    }

    public void setMissionNom(String missionNom) {
        this.missionNom = missionNom;
    }

    public Long getTypeEvenementId() {
        return typeEvenementId;
    }

    public void setTypeEvenementId(Long typeEvenementId) {
        this.typeEvenementId = typeEvenementId;
    }

    public String getTypeEvenementNom() {
        return typeEvenementNom;
    }

    public void setTypeEvenementNom(String typeEvenementNom) {
        this.typeEvenementNom = typeEvenementNom;
    }

    public String getNiveauPriorite() {
        return niveauPriorite;
    }

    public void setNiveauPriorite(String niveauPriorite) {
        this.niveauPriorite = niveauPriorite;
    }

    public String getNiveauPrioriteLabel() {
        return niveauPrioriteLabel;
    }

    public void setNiveauPrioriteLabel(String niveauPrioriteLabel) {
        this.niveauPrioriteLabel = niveauPrioriteLabel;
    }

    public String getNiveauPrioriteColor() {
        return niveauPrioriteColor;
    }

    public void setNiveauPrioriteColor(String niveauPrioriteColor) {
        this.niveauPrioriteColor = niveauPrioriteColor;
    }

    public boolean isUpcoming() {
        return isUpcoming;
    }

    public void setUpcoming(boolean upcoming) {
        isUpcoming = upcoming;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isPast() {
        return isPast;
    }

    public void setPast(boolean past) {
        isPast = past;
    }
}