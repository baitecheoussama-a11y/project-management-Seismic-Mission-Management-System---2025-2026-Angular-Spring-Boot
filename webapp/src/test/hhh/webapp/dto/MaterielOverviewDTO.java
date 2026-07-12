package com.pfe.webapp.dto;

import com.pfe.webapp.entity.materiel.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MaterielOverviewDTO {
    // Basic Info
    private Long idMateriel;
    private String codeMateriel;
    private String marque;
    private String modele;
    private String designation;
    private LocalDate dateAchat;
    private Double prix;
    private String status;
    private String categoryName;
    private String typeName;
    private boolean enUtilisation;

    // Images
    private List<ImageDTO> images;

    // Usage History (limited for overview)
    private List<UsageHistoryDTO> recentUsageHistory;
    private int totalUsageCount;

    // Repairs (limited for overview)
    private List<RepairDTO> recentRepairs;
    private int totalRepairCount;

    // Constructors, Getters, Setters
    public MaterielOverviewDTO() {}

    public MaterielOverviewDTO(Materiel materiel) {
        this.idMateriel = materiel.getIdMateriel();
        this.codeMateriel = materiel.getCodeMateriel();
        this.marque = materiel.getMarque();
        this.modele = materiel.getModele();
        this.designation = materiel.getDesignation();
        this.dateAchat = materiel.getDateAchat();
        this.prix = materiel.getPrix();
        this.status = materiel.getStatus().name();
        this.enUtilisation = materiel.isEnUtilisation();

        if (materiel.getTypeMateriel() != null) {
            this.typeName = materiel.getTypeMateriel().getLibelle();
            if (materiel.getTypeMateriel().getCategorie() != null) {
                this.categoryName = materiel.getTypeMateriel().getCategorie().getNom();
            }
        }
    }

    // Getters and Setters
    public Long getIdMateriel() { return idMateriel; }
    public void setIdMateriel(Long idMateriel) { this.idMateriel = idMateriel; }
    public String getCodeMateriel() { return codeMateriel; }
    public void setCodeMateriel(String codeMateriel) { this.codeMateriel = codeMateriel; }
    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public LocalDate getDateAchat() { return dateAchat; }
    public void setDateAchat(LocalDate dateAchat) { this.dateAchat = dateAchat; }
    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public boolean isEnUtilisation() { return enUtilisation; }
    public void setEnUtilisation(boolean enUtilisation) { this.enUtilisation = enUtilisation; }
    public List<ImageDTO> getImages() { return images; }
    public void setImages(List<ImageDTO> images) { this.images = images; }
    public List<UsageHistoryDTO> getRecentUsageHistory() { return recentUsageHistory; }
    public void setRecentUsageHistory(List<UsageHistoryDTO> recentUsageHistory) { this.recentUsageHistory = recentUsageHistory; }
    public int getTotalUsageCount() { return totalUsageCount; }
    public void setTotalUsageCount(int totalUsageCount) { this.totalUsageCount = totalUsageCount; }
    public List<RepairDTO> getRecentRepairs() { return recentRepairs; }
    public void setRecentRepairs(List<RepairDTO> recentRepairs) { this.recentRepairs = recentRepairs; }
    public int getTotalRepairCount() { return totalRepairCount; }
    public void setTotalRepairCount(int totalRepairCount) { this.totalRepairCount = totalRepairCount; }

    // Inner DTOs
    public static class ImageDTO {
        private Long idImage;
        private String imageUrl;
        private String fileName;
        private String contentType;

        public ImageDTO(MaterielImage image) {
            this.idImage = image.getIdImage();
            this.imageUrl = image.getImageUrl();
            this.fileName = image.getFileName();
            this.contentType = image.getContentType();
        }

        public Long getIdImage() { return idImage; }
        public void setIdImage(Long idImage) { this.idImage = idImage; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
    }

    public static class UsageHistoryDTO {
        private Long idUtilisation;
        private String resume;
        private LocalDate dateDebut;
        private LocalDate dateFin;
        private Double valeurUtilisation;
        private String uniteUtilisation;

        public UsageHistoryDTO(HistoriqueUtilisation history) {
            this.idUtilisation = history.getIdUtilisation();
            this.resume = history.getResume();
            this.dateDebut = history.getDateDebut();
            this.dateFin = history.getDateFin();
            this.valeurUtilisation = history.getValeurUtilisation();
            this.uniteUtilisation = history.getUniteUtilisation() != null ?
                    history.getUniteUtilisation().toString() : null;
        }

        public Long getIdUtilisation() { return idUtilisation; }
        public void setIdUtilisation(Long idUtilisation) { this.idUtilisation = idUtilisation; }
        public String getResume() { return resume; }
        public void setResume(String resume) { this.resume = resume; }
        public LocalDate getDateDebut() { return dateDebut; }
        public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
        public LocalDate getDateFin() { return dateFin; }
        public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
        public Double getValeurUtilisation() { return valeurUtilisation; }
        public void setValeurUtilisation(Double valeurUtilisation) { this.valeurUtilisation = valeurUtilisation; }
        public String getUniteUtilisation() { return uniteUtilisation; }
        public void setUniteUtilisation(String uniteUtilisation) { this.uniteUtilisation = uniteUtilisation; }
    }

    public static class RepairDTO {
        private Long idReparation;
        private String type;
        private Double cout;
        private LocalDate datePanne;
        private LocalDate dateReparation;
        private String detailProbleme;
        private String technicien;
        private String fournisseur;
        private LocalDate dateSortieChantier;
        private LocalDate dateEntreeChantier;

        public RepairDTO(Reparation reparation) {
            this.idReparation = reparation.getIdReparation();
            this.cout = reparation.getCout();
            this.datePanne = reparation.getDatePanne();
            this.dateReparation = reparation.getDateReparation();
            this.detailProbleme = reparation.getDetailProbleme();

            if (reparation instanceof ReparationInterne) {
                this.type = "INTERNE";
                this.technicien = ((ReparationInterne) reparation).getTechnicien();
            } else if (reparation instanceof ReparationExterne) {
                this.type = "EXTERNE";
                ReparationExterne ext = (ReparationExterne) reparation;
                this.fournisseur = ext.getFournisseur();
                this.dateSortieChantier = ext.getDateSortieChantier();
                this.dateEntreeChantier = ext.getDateEntreeChantier();
            }
        }

        public Long getIdReparation() { return idReparation; }
        public void setIdReparation(Long idReparation) { this.idReparation = idReparation; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Double getCout() { return cout; }
        public void setCout(Double cout) { this.cout = cout; }
        public LocalDate getDatePanne() { return datePanne; }
        public void setDatePanne(LocalDate datePanne) { this.datePanne = datePanne; }
        public LocalDate getDateReparation() { return dateReparation; }
        public void setDateReparation(LocalDate dateReparation) { this.dateReparation = dateReparation; }
        public String getDetailProbleme() { return detailProbleme; }
        public void setDetailProbleme(String detailProbleme) { this.detailProbleme = detailProbleme; }
        public String getTechnicien() { return technicien; }
        public void setTechnicien(String technicien) { this.technicien = technicien; }
        public String getFournisseur() { return fournisseur; }
        public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }
        public LocalDate getDateSortieChantier() { return dateSortieChantier; }
        public void setDateSortieChantier(LocalDate dateSortieChantier) { this.dateSortieChantier = dateSortieChantier; }
        public LocalDate getDateEntreeChantier() { return dateEntreeChantier; }
        public void setDateEntreeChantier(LocalDate dateEntreeChantier) { this.dateEntreeChantier = dateEntreeChantier; }
    }
}