// MaterielDetailDTO.java (for full details when clicking "Show More")
package com.pfe.webapp.dto;

import com.pfe.webapp.entity.materiel.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MaterielDetailDTO extends MaterielOverviewDTO {
    private List<UsageHistoryDTO> allUsageHistory;
    private List<RepairDTO> allRepairs;

    public MaterielDetailDTO(Materiel materiel) {
        super(materiel);
    }

    // Getters and Setters
    public List<UsageHistoryDTO> getAllUsageHistory() { return allUsageHistory; }
    public void setAllUsageHistory(List<UsageHistoryDTO> allUsageHistory) { this.allUsageHistory = allUsageHistory; }
    public List<RepairDTO> getAllRepairs() { return allRepairs; }
    public void setAllRepairs(List<RepairDTO> allRepairs) { this.allRepairs = allRepairs; }
}