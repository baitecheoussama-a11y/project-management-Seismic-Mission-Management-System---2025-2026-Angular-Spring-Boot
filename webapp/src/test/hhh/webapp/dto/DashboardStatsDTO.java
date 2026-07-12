package com.pfe.webapp.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDTO {
    // الإحصائيات الأساسية
    private long totalEmployees;
    private long totalAccounts;
    private long totalContracts;
    private long totalRoles;
    private long activeAccounts;
    private long inactiveAccounts;

    // إحصائيات العقود حسب النوع
    private Map<String, Long> contractsByType;

    // إحصائيات الموظفين حسب الجنس
    private Map<String, Long> employeesByGender;

    // أحدث الموظفين
    private List<RecentEmployeeDTO> recentEmployees;

    // توزيع الأدوار
    private Map<String, Long> rolesDistribution;

    // Getters and Setters
    public long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(long totalEmployees) { this.totalEmployees = totalEmployees; }

    public long getTotalAccounts() { return totalAccounts; }
    public void setTotalAccounts(long totalAccounts) { this.totalAccounts = totalAccounts; }

    public long getTotalContracts() { return totalContracts; }
    public void setTotalContracts(long totalContracts) { this.totalContracts = totalContracts; }

    public long getTotalRoles() { return totalRoles; }
    public void setTotalRoles(long totalRoles) { this.totalRoles = totalRoles; }

    public long getActiveAccounts() { return activeAccounts; }
    public void setActiveAccounts(long activeAccounts) { this.activeAccounts = activeAccounts; }

    public long getInactiveAccounts() { return inactiveAccounts; }
    public void setInactiveAccounts(long inactiveAccounts) { this.inactiveAccounts = inactiveAccounts; }

    public Map<String, Long> getContractsByType() { return contractsByType; }
    public void setContractsByType(Map<String, Long> contractsByType) { this.contractsByType = contractsByType; }

    public Map<String, Long> getEmployeesByGender() { return employeesByGender; }
    public void setEmployeesByGender(Map<String, Long> employeesByGender) { this.employeesByGender = employeesByGender; }

    public List<RecentEmployeeDTO> getRecentEmployees() { return recentEmployees; }
    public void setRecentEmployees(List<RecentEmployeeDTO> recentEmployees) { this.recentEmployees = recentEmployees; }

    public Map<String, Long> getRolesDistribution() { return rolesDistribution; }
    public void setRolesDistribution(Map<String, Long> rolesDistribution) { this.rolesDistribution = rolesDistribution; }

    // Inner class for recent employees
    public static class RecentEmployeeDTO {
        private Long id;
        private String nom;
        private String prenom;
        private String email;
        private String dateCreation;

        public RecentEmployeeDTO(Long id, String nom, String prenom, String email, String dateCreation) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.email = email;
            this.dateCreation = dateCreation;
        }

        // Getters
        public Long getId() { return id; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getEmail() { return email; }
        public String getDateCreation() { return dateCreation; }
    }
}