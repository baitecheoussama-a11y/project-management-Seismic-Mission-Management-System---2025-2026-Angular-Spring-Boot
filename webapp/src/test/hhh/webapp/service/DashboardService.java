package com.pfe.webapp.service;

import com.pfe.webapp.dto.DashboardStatsDTO;
import com.pfe.webapp.entity.StatusCompte;
import com.pfe.webapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AffectationRoleRepository affectationRoleRepository;

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // إحصائيات الموظفين
        stats.setTotalEmployees(employeRepository.count());
        stats.setTotalAccounts(compteRepository.count());
        stats.setTotalContracts(contratRepository.count());
        stats.setTotalRoles(roleRepository.count());

        // ✅ إصلاح: الحسابات النشطة وغير النشطة
        long activeAccounts = compteRepository.countByStatus(StatusCompte.ACTIVE);
        stats.setActiveAccounts(activeAccounts);
        stats.setInactiveAccounts(stats.getTotalAccounts() - activeAccounts);

        // ✅ إصلاح: العقود حسب النوع
        List<Object[]> contractsResult = contratRepository.countByType();
        Map<String, Long> contractsByType = new HashMap<>();
        for (Object[] row : contractsResult) {
            String type = row[0] != null ? row[0].toString() : "AUTRE";
            Long count = ((Number) row[1]).longValue();
            contractsByType.put(type, count);
        }
        stats.setContractsByType(contractsByType);

        // ✅ إصلاح: الموظفين حسب الجنس
        List<Object[]> genderResult = employeRepository.countBySexe();
        Map<String, Long> employeesByGender = new HashMap<>();
        for (Object[] row : genderResult) {
            String gender = row[0] != null ? row[0].toString() : "AUTRE";
            Long count = ((Number) row[1]).longValue();
            employeesByGender.put(gender, count);
        }
        stats.setEmployeesByGender(employeesByGender);

        // ✅ إصلاح: أحدث 5 موظفين
        List<Object[]> recentData = employeRepository.findTop5ByOrderByIdDesc();
        List<DashboardStatsDTO.RecentEmployeeDTO> recentEmployees = recentData.stream()
                .map(data -> new DashboardStatsDTO.RecentEmployeeDTO(
                        ((Number) data[0]).longValue(),
                        (String) data[1],
                        (String) data[2],
                        (String) data[3],
                        data[4] != null ? data[4].toString() : ""
                ))
                .collect(Collectors.toList());
        stats.setRecentEmployees(recentEmployees);

        // ✅ إصلاح: توزيع الأدوار
        List<Object[]> rolesResult = affectationRoleRepository.countRolesDistribution();
        Map<String, Long> rolesDistribution = new HashMap<>();
        for (Object[] row : rolesResult) {
            String roleType = row[0] != null ? row[0].toString() : "AUTRE";
            Long count = ((Number) row[1]).longValue();
            rolesDistribution.put(roleType, count);
        }
        stats.setRolesDistribution(rolesDistribution);

        return stats;
    }
}