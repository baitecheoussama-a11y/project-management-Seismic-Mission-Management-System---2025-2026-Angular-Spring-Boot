// dto/pointage/PointageStatsDTO.java
package com.pfe.webapp.dto.pointage;

public class PointageStatsDTO {
    private long totalEmployees;
    private long present;
    private long absent;
    private long late;
    private long onLeave;
    private long onMission;
    private long notRecorded;

    // Getters and Setters
    public long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(long totalEmployees) { this.totalEmployees = totalEmployees; }

    public long getPresent() { return present; }
    public void setPresent(long present) { this.present = present; }

    public long getAbsent() { return absent; }
    public void setAbsent(long absent) { this.absent = absent; }

    public long getLate() { return late; }
    public void setLate(long late) { this.late = late; }

    public long getOnLeave() { return onLeave; }
    public void setOnLeave(long onLeave) { this.onLeave = onLeave; }

    public long getOnMission() { return onMission; }
    public void setOnMission(long onMission) { this.onMission = onMission; }

    public long getNotRecorded() { return notRecorded; }
    public void setNotRecorded(long notRecorded) { this.notRecorded = notRecorded; }
}