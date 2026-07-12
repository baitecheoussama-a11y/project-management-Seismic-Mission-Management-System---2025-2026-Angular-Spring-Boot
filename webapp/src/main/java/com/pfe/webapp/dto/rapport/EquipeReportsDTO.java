// dto/team/EquipeReportsDTO.java
package com.pfe.webapp.dto.team;

import com.pfe.webapp.entity.TypeActivite;

public class EquipeReportsDTO {
    private Long id;
    private String nom;
    private TypeActivite type;
    private int memberCount;
    private int reportCount;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public TypeActivite getType() { return type; }
    public void setType(TypeActivite type) { this.type = type; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public int getReportCount() { return reportCount; }
    public void setReportCount(int reportCount) { this.reportCount = reportCount; }
}