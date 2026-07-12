// dto/project/ProjectProgressStatsDTO.java - أضف الحقول الجديدة
package com.pfe.webapp.dto.project;

import java.util.List;

public class ProjectProgressStatsDTO {

    // إحصائيات الأنشطة
    private int totalActivities;
    private int completedActivities;
    private int inProgressActivities;
    private int pendingActivities;
    private int delayedActivities;  // ✅ جديد

    // إحصائيات الإنتاجية وساعات العمل
    private double totalWorkHours;
    private double averageProductivity;
    private double totalProductivityValue;

    // إحصائيات التقارير
    private int totalReports;
    private int totalRendements;

    // قائمة الأنشطة مع تفاصيلها
    private List<ActivityProgressDTO> activitiesProgress;

    // Getters and Setters
    public int getTotalActivities() { return totalActivities; }
    public void setTotalActivities(int totalActivities) { this.totalActivities = totalActivities; }

    public int getCompletedActivities() { return completedActivities; }
    public void setCompletedActivities(int completedActivities) { this.completedActivities = completedActivities; }

    public int getInProgressActivities() { return inProgressActivities; }
    public void setInProgressActivities(int inProgressActivities) { this.inProgressActivities = inProgressActivities; }

    public int getPendingActivities() { return pendingActivities; }
    public void setPendingActivities(int pendingActivities) { this.pendingActivities = pendingActivities; }

    public int getDelayedActivities() { return delayedActivities; }
    public void setDelayedActivities(int delayedActivities) { this.delayedActivities = delayedActivities; }

    public double getTotalWorkHours() { return totalWorkHours; }
    public void setTotalWorkHours(double totalWorkHours) { this.totalWorkHours = totalWorkHours; }

    public double getAverageProductivity() { return averageProductivity; }
    public void setAverageProductivity(double averageProductivity) { this.averageProductivity = averageProductivity; }

    public double getTotalProductivityValue() { return totalProductivityValue; }
    public void setTotalProductivityValue(double totalProductivityValue) { this.totalProductivityValue = totalProductivityValue; }

    public int getTotalReports() { return totalReports; }
    public void setTotalReports(int totalReports) { this.totalReports = totalReports; }

    public int getTotalRendements() { return totalRendements; }
    public void setTotalRendements(int totalRendements) { this.totalRendements = totalRendements; }

    public List<ActivityProgressDTO> getActivitiesProgress() { return activitiesProgress; }
    public void setActivitiesProgress(List<ActivityProgressDTO> activitiesProgress) { this.activitiesProgress = activitiesProgress; }

    // Helper methods
    public String getCompletionRate() {
        if (totalActivities == 0) return "0%";
        return String.format("%.1f%%", (completedActivities * 100.0 / totalActivities));
    }

    public String getProgressStatus() {
        if (completedActivities == totalActivities && totalActivities > 0) return "completed";
        if (delayedActivities > 0) return "delayed";
        if (inProgressActivities > 0) return "in-progress";
        return "pending";
    }
}