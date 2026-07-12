package com.pfe.webapp.dto.team;

public class ActiveSimpleDTO {
    private Long id;
    private String codeActive;
    private String objectif;
    private String description;
    private Integer teamCount;
    private String missionCode;  // Add this
    private Long missionId;       // Add this

    // Constructors
    public ActiveSimpleDTO(Long id, String codeActive, String objectif,
                           String description, Integer teamCount) {
        this.id = id;
        this.codeActive = codeActive;
        this.objectif = objectif;
        this.description = description;
        this.teamCount = teamCount;
    }

    public ActiveSimpleDTO(Long id, String codeActive, String objectif,
                           String description, Integer teamCount,
                           String missionCode, Long missionId) {
        this(id, codeActive, objectif, description, teamCount);
        this.missionCode = missionCode;
        this.missionId = missionId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodeActive() { return codeActive; }
    public void setCodeActive(String codeActive) { this.codeActive = codeActive; }

    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getTeamCount() { return teamCount; }
    public void setTeamCount(Integer teamCount) { this.teamCount = teamCount; }

    public String getMissionCode() { return missionCode; }
    public void setMissionCode(String missionCode) { this.missionCode = missionCode; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }
}