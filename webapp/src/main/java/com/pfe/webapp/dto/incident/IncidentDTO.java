package com.pfe.webapp.dto.incident;

import java.time.LocalDate;

public class IncidentDTO {
    private Long id;
    private String type;
    private String typeLabel;
    private String typeColor;
    private String description;
    private LocalDate dateIncident;
    private String niveauGravite;
    private String niveauGraviteLabel;
    private String niveauGraviteColor;
    private Long employeId;
    private String employeNom;
    private String employePrenom;
    private String employeEmail;
    private Long etatMedicalId;
    private String groupeSanguin;
    private Long coordonneeId;
    private Double latitude;
    private Double longitude;
    private Integer ordre;
    private Long siteId;
    private Double siteSurface;
    private Integer wilayaNum;
    private String wilayaNom;

    // Constructors
    public IncidentDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTypeLabel() { return typeLabel; }
    public void setTypeLabel(String typeLabel) { this.typeLabel = typeLabel; }

    public String getTypeColor() { return typeColor; }
    public void setTypeColor(String typeColor) { this.typeColor = typeColor; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateIncident() { return dateIncident; }
    public void setDateIncident(LocalDate dateIncident) { this.dateIncident = dateIncident; }

    public String getNiveauGravite() { return niveauGravite; }
    public void setNiveauGravite(String niveauGravite) { this.niveauGravite = niveauGravite; }

    public String getNiveauGraviteLabel() { return niveauGraviteLabel; }
    public void setNiveauGraviteLabel(String niveauGraviteLabel) { this.niveauGraviteLabel = niveauGraviteLabel; }

    public String getNiveauGraviteColor() { return niveauGraviteColor; }
    public void setNiveauGraviteColor(String niveauGraviteColor) { this.niveauGraviteColor = niveauGraviteColor; }

    public Long getEmployeId() { return employeId; }
    public void setEmployeId(Long employeId) { this.employeId = employeId; }

    public String getEmployeNom() { return employeNom; }
    public void setEmployeNom(String employeNom) { this.employeNom = employeNom; }

    public String getEmployePrenom() { return employePrenom; }
    public void setEmployePrenom(String employePrenom) { this.employePrenom = employePrenom; }

    public String getEmployeEmail() { return employeEmail; }
    public void setEmployeEmail(String employeEmail) { this.employeEmail = employeEmail; }

    public Long getEtatMedicalId() { return etatMedicalId; }
    public void setEtatMedicalId(Long etatMedicalId) { this.etatMedicalId = etatMedicalId; }

    public String getGroupeSanguin() { return groupeSanguin; }
    public void setGroupeSanguin(String groupeSanguin) { this.groupeSanguin = groupeSanguin; }

    public Long getCoordonneeId() { return coordonneeId; }
    public void setCoordonneeId(Long coordonneeId) { this.coordonneeId = coordonneeId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }

    public Double getSiteSurface() { return siteSurface; }
    public void setSiteSurface(Double siteSurface) { this.siteSurface = siteSurface; }

    public Integer getWilayaNum() { return wilayaNum; }
    public void setWilayaNum(Integer wilayaNum) { this.wilayaNum = wilayaNum; }

    public String getWilayaNom() { return wilayaNom; }
    public void setWilayaNom(String wilayaNom) { this.wilayaNom = wilayaNom; }
}