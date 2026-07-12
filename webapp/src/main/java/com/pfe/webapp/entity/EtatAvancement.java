    // entity/EtatAvancement.java
    package com.pfe.webapp.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.pfe.webapp.entity.StatusEtatAvancement;
    import jakarta.persistence.*;
    import java.time.LocalDate;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    public class EtatAvancement {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private LocalDate dateLastAvancement;

        @Enumerated(EnumType.STRING)
        private StatusEtatAvancement status;

        @ManyToOne
        @JoinColumn(name = "project_id")
        @JsonIgnore
        private Project project;

        @ManyToOne
        @JoinColumn(name = "active_id")
        @JsonIgnore
        private Active active;

        @OneToMany(mappedBy = "etatAvancement", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Avancement> avancements = new ArrayList<>();

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public LocalDate getDateLastAvancement() { return dateLastAvancement; }
        public void setDateLastAvancement(LocalDate dateLastAvancement) { this.dateLastAvancement = dateLastAvancement; }

        public StatusEtatAvancement getStatus() { return status; }
        public void setStatus(StatusEtatAvancement status) { this.status = status; }

        public Project getProject() { return project; }
        public void setProject(Project project) { this.project = project; }

        public Active getActive() { return active; }
        public void setActive(Active active) { this.active = active; }

        public List<Avancement> getAvancements() { return avancements; }
        public void setAvancements(List<Avancement> avancements) { this.avancements = avancements; }

        // Helper methods
        public void addAvancement(Avancement avancement) {
            avancements.add(avancement);
            avancement.setEtatAvancement(this);
            this.dateLastAvancement = avancement.getDate();
        }

        public void removeAvancement(Avancement avancement) {
            avancements.remove(avancement);
            avancement.setEtatAvancement(null);
        }
    }