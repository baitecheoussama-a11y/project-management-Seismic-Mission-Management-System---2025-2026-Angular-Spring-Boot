package com.pfe.webapp.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Role extends BaseEntity {  // ✅ extends BaseEntity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TypeRole type;

    @OneToMany(mappedBy = "role")
    private List<AffectationRole> comptes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TypeRole getType() { return type; }
    public void setType(TypeRole type) { this.type = type; }

    public List<AffectationRole> getComptes() { return comptes; }
    public void setComptes(List<AffectationRole> comptes) { this.comptes = comptes; }
}