// dto/project/ActivityProgressDTO.java
package com.pfe.webapp.dto.project;

public class ActivityProgressDTO {
    private Long activeId;
    private String codeActive;
    private String objectif;
    private String status; // COMPLETED, IN_PROGRESS, PENDING
    private double productivityValue;
    private double totalWorkHours;
    private int rendementCount;
    private double completionPercentage;

    // Getters and Setters
    public Long getActiveId() { return activeId; }
    public void setActiveId(Long activeId) { this.activeId = activeId; }

    public String getCodeActive() { return codeActive; }
    public void setCodeActive(String codeActive) { this.codeActive = codeActive; }

    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getProductivityValue() { return productivityValue; }
    public void setProductivityValue(double productivityValue) { this.productivityValue = productivityValue; }

    public double getTotalWorkHours() { return totalWorkHours; }
    public void setTotalWorkHours(double totalWorkHours) { this.totalWorkHours = totalWorkHours; }

    public int getRendementCount() { return rendementCount; }
    public void setRendementCount(int rendementCount) { this.rendementCount = rendementCount; }

    public double getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(double completionPercentage) { this.completionPercentage = completionPercentage; }

    // Helper method to get status label
    public String getStatusLabel() {
        switch (status) {
            case "COMPLETED": return "Completed";
            case "IN_PROGRESS": return "In Progress";
            default: return "Pending";
        }
    }
}