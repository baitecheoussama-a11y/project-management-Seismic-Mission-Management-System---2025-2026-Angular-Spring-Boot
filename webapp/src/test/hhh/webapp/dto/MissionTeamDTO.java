// dto/MissionTeamDTO.java
package com.pfe.webapp.dto;

import java.util.List;
import java.util.Map;

public class MissionTeamDTO {
    private Long missionId;
    private String missionName;
    private int totalMembers;
    private List<EmployeDTO> members;
    private Map<String, List<EmployeDTO>> membersByEquipe;
    private List<EquipeDTO> equipes;

    // Getters and Setters
    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getMissionName() { return missionName; }
    public void setMissionName(String missionName) { this.missionName = missionName; }

    public int getTotalMembers() { return totalMembers; }
    public void setTotalMembers(int totalMembers) { this.totalMembers = totalMembers; }

    public List<EmployeDTO> getMembers() { return members; }
    public void setMembers(List<EmployeDTO> members) { this.members = members; }

    public Map<String, List<EmployeDTO>> getMembersByEquipe() { return membersByEquipe; }
    public void setMembersByEquipe(Map<String, List<EmployeDTO>> membersByEquipe) { this.membersByEquipe = membersByEquipe; }

    public List<EquipeDTO> getEquipes() { return equipes; }
    public void setEquipes(List<EquipeDTO> equipes) { this.equipes = equipes; }
}