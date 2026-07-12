package com.pfe.webapp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class AffectationRole extends BaseEntity {  // ✅ extends BaseEntity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "compte_id")
    private Compte compte;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Compte getCompte() { return compte; }
    public void setCompte(Compte compte) { this.compte = compte; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}