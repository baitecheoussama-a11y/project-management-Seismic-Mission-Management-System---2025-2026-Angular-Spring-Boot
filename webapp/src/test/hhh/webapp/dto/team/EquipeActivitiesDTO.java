// dto/team/EquipeActivitiesDTO.java
package com.pfe.webapp.dto.team;

import com.pfe.webapp.entity.TypeActivite;

public class EquipeActivitiesDTO {
    private Long id;
    private String nom;
    private TypeActivite type;
    private int memberCount;
    private int activitiesCount;

    // ✅ أضف هذا Constructor
    public EquipeActivitiesDTO() {
    }

    // ✅ أضف هذا Constructor مع جميع المعاملات
    public EquipeActivitiesDTO(Long id, String nom, TypeActivite type, long memberCount, long activitiesCount) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.memberCount = (int) memberCount;
        this.activitiesCount = (int) activitiesCount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public TypeActivite getType() { return type; }
    public void setType(TypeActivite type) { this.type = type; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public int getActivitiesCount() { return activitiesCount; }
    public void setActivitiesCount(int activitiesCount) { this.activitiesCount = activitiesCount; }
}