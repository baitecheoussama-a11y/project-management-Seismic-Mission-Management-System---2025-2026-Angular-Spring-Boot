// dto/EquipeDTO.java (تأكد من وجود memberCount)
package com.pfe.webapp.dto;

import com.pfe.webapp.entity.TypeActivite;
import java.util.List;

public class EquipeDTO {
    private Long id;
    private String nom;
    private TypeActivite type;
    private int memberCount;
    private List<EmployeDTO> members;

    // dto/EquipeDTO.java - Add this field
    private Double averageProductivity;

    // Constructors
    public EquipeDTO() {}

    public EquipeDTO(Long id, String nom, TypeActivite type, int memberCount) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.memberCount = memberCount;
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

    public List<EmployeDTO> getMembers() { return members; }
    public void setMembers(List<EmployeDTO> members) { this.members = members; }

    // Add getter and setter
    public Double getAverageProductivity() { return averageProductivity; }
    public void setAverageProductivity(Double averageProductivity) { this.averageProductivity = averageProductivity; }

}