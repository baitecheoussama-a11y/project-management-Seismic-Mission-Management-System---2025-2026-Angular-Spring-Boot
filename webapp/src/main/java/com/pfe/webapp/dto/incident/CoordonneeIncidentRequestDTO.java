package com.pfe.webapp.dto.incident;

public class CoordonneeIncidentRequestDTO {
    private Double latitude;
    private Double longitude;
    private Integer ordre;
    private Long siteId;  // Site ID (الموقع الذي تختاره)

    // Getters and Setters
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
}