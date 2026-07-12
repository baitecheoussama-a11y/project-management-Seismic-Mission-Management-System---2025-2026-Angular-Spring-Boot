// src/main/java/com/pfe/webapp/service/pivot/PivotServiceImpl.java
package com.pfe.webapp.service.pivot;

import com.pfe.webapp.dto.pivot.PivotRequestDTO;
import com.pfe.webapp.dto.pivot.PivotResponseDTO;
import com.pfe.webapp.dto.pivot.PivotRowDTO;
import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.entity.Project;
import com.pfe.webapp.repository.MissionRepository;
import com.pfe.webapp.repository.ProjectRepository;
import com.pfe.webapp.repository.ressource.ConsommationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PivotServiceImpl implements PivotService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ConsommationRepository consommationRepository;

    @Override
    @Transactional(readOnly = true)
    public PivotResponseDTO getPivotData(PivotRequestDTO request) {
        // 1. Fetch data based on filters
        List<PivotDataItem> dataItems = fetchPivotData(request);

        // 2. Extract row and column values
        Set<String> rowValues = new HashSet<>();
        Set<String> colValues = new HashSet<>();

        for (PivotDataItem item : dataItems) {
            String rowKey = getRowKey(item, request.getRowField());
            String colKey = getColumnKey(item, request.getColField());
            if (rowKey != null) rowValues.add(rowKey);
            if (colKey != null) colValues.add(colKey);
        }

        List<String> sortedRowValues = new ArrayList<>(rowValues);
        Collections.sort(sortedRowValues);

        List<String> sortedColValues = new ArrayList<>(colValues);
        Collections.sort(sortedColValues);

        // 3. Build pivot table
        List<PivotRowDTO> rows = new ArrayList<>();
        Map<String, Double> columnTotals = new HashMap<>();
        double grandTotal = 0.0;

        for (String rowVal : sortedRowValues) {
            Map<String, Double> rowValuesMap = new LinkedHashMap<>();
            double rowTotal = 0.0;

            for (String colVal : sortedColValues) {
                // Filter data for this row and column
                List<Double> values = dataItems.stream()
                        .filter(item -> {
                            String rKey = getRowKey(item, request.getRowField());
                            String cKey = getColumnKey(item, request.getColField());
                            return rKey != null && rKey.equals(rowVal) &&
                                    cKey != null && cKey.equals(colVal);
                        })
                        .map(item -> getValue(item, request.getValueField()))
                        .filter(v -> v != null)
                        .collect(Collectors.toList());

                Double aggregatedValue = aggregateValues(values, request.getAggregator());
                rowValuesMap.put(colVal, aggregatedValue);

                rowTotal += aggregatedValue;
                columnTotals.merge(colVal, aggregatedValue, Double::sum);
                grandTotal += aggregatedValue;
            }

            PivotRowDTO row = new PivotRowDTO();
            row.setRowLabel(rowVal);
            row.setValues(rowValuesMap);
            row.setRowTotal(rowTotal);
            row.setIsTotal(false);
            rows.add(row);
        }

        // 4. Add total row
        PivotRowDTO totalRow = new PivotRowDTO();
        totalRow.setRowLabel("Total");
        totalRow.setValues(new HashMap<>(columnTotals));
        totalRow.setRowTotal(grandTotal);
        totalRow.setIsTotal(true);
        rows.add(totalRow);

        // 5. Build response
        PivotResponseDTO response = new PivotResponseDTO();
        response.setHeaders(sortedColValues);
        response.setRows(rows);
        response.setColumnTotals(columnTotals);
        response.setGrandTotal(grandTotal);
        response.setTotalRows(sortedRowValues.size());
        response.setTotalColumns(sortedColValues.size());
        response.setAggregator(request.getAggregator());
        response.setValueField(request.getValueField());
        response.setRowField(request.getRowField());
        response.setColField(request.getColField());

        return response;
    }

    private List<PivotDataItem> fetchPivotData(PivotRequestDTO request) {
        List<PivotDataItem> items = new ArrayList<>();

        // Get all missions
        List<Mission> missions = missionRepository.findAll();

        for (Mission mission : missions) {
            // Filter by mission ID if provided
            if (request.getMissionId() != null && !mission.getId().equals(request.getMissionId())) {
                continue;
            }

            // Get projects for this mission
            List<Project> projects = projectRepository.findByMissionId(mission.getId());

            if (projects == null || projects.isEmpty()) {
                // Add a data item with zero values if no projects
                items.add(createPivotDataItem(mission, null));
                continue;
            }

            // Filter projects by status if provided
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                projects = projects.stream()
                        .filter(p -> request.getStatus().equals(p.calculateStatus()))
                        .collect(Collectors.toList());
            }

            // Filter projects by year if provided
            if (request.getYear() != null) {
                projects = projects.stream()
                        .filter(p -> {
                            if (p.getObjectifDebut() != null) {
                                return p.getObjectifDebut().getYear() == request.getYear();
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
            }

            // Create data items for each project
            for (Project project : projects) {
                items.add(createPivotDataItem(mission, project));
            }
        }

        return items;
    }

    private PivotDataItem createPivotDataItem(Mission mission, Project project) {
        PivotDataItem item = new PivotDataItem();

        // Mission fields
        item.setMissionCode(mission.getCodeMission());
        item.setMethodologie(mission.getMethodologie() != null ? mission.getMethodologie().name() : "N/A");
        item.setMissionId(mission.getId());
        item.setMissionDescription(mission.getDescription());

        if (project != null) {
            // Project fields
            item.setProjectId(project.getId());
            item.setProjectName(project.getNom());
            item.setProjectDescription(project.getDescription());
            item.setStatus(project.calculateStatus());
            item.setBudget(project.getBudget() != null ? project.getBudget() : 0.0);
            item.setProgression(project.getProgression() != null ? project.getProgression().doubleValue() : 0.0);
            item.setObjectifVP(project.getObjectifVP() != null ? project.getObjectifVP() : 0);
            item.setAnnule(project.getAnnule() != null && project.getAnnule());

            // Dates
            if (project.getObjectifDebut() != null) {
                item.setObjectifDebut(project.getObjectifDebut());
                item.setMonth(project.getObjectifDebut().getMonth().toString());
                item.setQuarter("Q" + ((project.getObjectifDebut().getMonthValue() - 1) / 3 + 1));
                item.setYear(project.getObjectifDebut().getYear());
            }
            if (project.getObjectifFin() != null) {
                item.setObjectifFin(project.getObjectifFin());
            }
            if (project.getDateStartReelle() != null) {
                item.setDateStartReelle(project.getDateStartReelle());
            }
            if (project.getDateFinReelle() != null) {
                item.setDateFinReelle(project.getDateFinReelle());
            }

            // Calculate total cost from consumptions
            Double totalCost = consommationRepository.getTotalCostByMission(mission.getId());
            item.setTotalCost(totalCost != null ? totalCost : 0.0);

            // Project count for this mission
            List<Project> missionProjects = projectRepository.findByMissionId(mission.getId());
            item.setProjectCount(missionProjects != null ? missionProjects.size() : 0);
        } else {
            // No project - set default values
            item.setStatus("PLANIFIER");
            item.setBudget(0.0);
            item.setProgression(0.0);
            item.setObjectifVP(0);
            item.setAnnule(false);
            item.setTotalCost(0.0);
            item.setProjectCount(0);

            // Use current date for month/quarter/year
            LocalDate now = LocalDate.now();
            item.setMonth(now.getMonth().toString());
            item.setQuarter("Q" + ((now.getMonthValue() - 1) / 3 + 1));
            item.setYear(now.getYear());
        }

        return item;
    }

    private String getRowKey(PivotDataItem item, String rowField) {
        switch (rowField) {
            case "missionCode": return item.getMissionCode();
            case "methodologie": return item.getMethodologie();
            case "status": return item.getStatus();
            case "month": return item.getMonth();
            case "quarter": return item.getQuarter();
            case "year": return item.getYear() != null ? String.valueOf(item.getYear()) : null;
            case "projectName": return item.getProjectName();
            default: return item.getMissionCode();
        }
    }

    private String getColumnKey(PivotDataItem item, String colField) {
        switch (colField) {
            case "missionCode": return item.getMissionCode();
            case "methodologie": return item.getMethodologie();
            case "status": return item.getStatus();
            case "month": return item.getMonth();
            case "quarter": return item.getQuarter();
            case "year": return item.getYear() != null ? String.valueOf(item.getYear()) : null;
            case "projectName": return item.getProjectName();
            default: return item.getMonth();
        }
    }

    private Double getValue(PivotDataItem item, String valueField) {
        switch (valueField) {
            case "totalCost": return item.getTotalCost();
            case "projectCount": return Double.valueOf(item.getProjectCount());
            case "avgProgression": return item.getProgression();
            case "budget": return item.getBudget();
            case "objectifVP": return Double.valueOf(item.getObjectifVP());
            default: return item.getTotalCost();
        }
    }

    private Double aggregateValues(List<Double> values, String aggregator) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        switch (aggregator.toLowerCase()) {
            case "sum":
                return values.stream().mapToDouble(Double::doubleValue).sum();
            case "avg":
                return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            case "count":
                return (double) values.size();
            case "min":
                return values.stream().min(Double::compareTo).orElse(0.0);
            case "max":
                return values.stream().max(Double::compareTo).orElse(0.0);
            default:
                return values.stream().mapToDouble(Double::doubleValue).sum();
        }
    }

    // Inner class for pivot data items
    private static class PivotDataItem {
        private Long missionId;
        private String missionCode;
        private String methodologie;
        private String missionDescription;

        private Long projectId;
        private String projectName;
        private String projectDescription;
        private String status;
        private Double budget;
        private Double progression;
        private Integer objectifVP;
        private Boolean annule;
        private Double totalCost;
        private Integer projectCount;

        private LocalDate objectifDebut;
        private LocalDate objectifFin;
        private LocalDate dateStartReelle;
        private LocalDate dateFinReelle;
        private String month;
        private String quarter;
        private Integer year;

        // Getters and Setters
        public Long getMissionId() { return missionId; }
        public void setMissionId(Long missionId) { this.missionId = missionId; }

        public String getMissionCode() { return missionCode; }
        public void setMissionCode(String missionCode) { this.missionCode = missionCode; }

        public String getMethodologie() { return methodologie; }
        public void setMethodologie(String methodologie) { this.methodologie = methodologie; }

        public String getMissionDescription() { return missionDescription; }
        public void setMissionDescription(String missionDescription) { this.missionDescription = missionDescription; }

        public Long getProjectId() { return projectId; }
        public void setProjectId(Long projectId) { this.projectId = projectId; }

        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }

        public String getProjectDescription() { return projectDescription; }
        public void setProjectDescription(String projectDescription) { this.projectDescription = projectDescription; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Double getBudget() { return budget; }
        public void setBudget(Double budget) { this.budget = budget; }

        public Double getProgression() { return progression; }
        public void setProgression(Double progression) { this.progression = progression; }

        public Integer getObjectifVP() { return objectifVP; }
        public void setObjectifVP(Integer objectifVP) { this.objectifVP = objectifVP; }

        public Boolean getAnnule() { return annule; }
        public void setAnnule(Boolean annule) { this.annule = annule; }

        public Double getTotalCost() { return totalCost; }
        public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

        public Integer getProjectCount() { return projectCount; }
        public void setProjectCount(Integer projectCount) { this.projectCount = projectCount; }

        public LocalDate getObjectifDebut() { return objectifDebut; }
        public void setObjectifDebut(LocalDate objectifDebut) { this.objectifDebut = objectifDebut; }

        public LocalDate getObjectifFin() { return objectifFin; }
        public void setObjectifFin(LocalDate objectifFin) { this.objectifFin = objectifFin; }

        public LocalDate getDateStartReelle() { return dateStartReelle; }
        public void setDateStartReelle(LocalDate dateStartReelle) { this.dateStartReelle = dateStartReelle; }

        public LocalDate getDateFinReelle() { return dateFinReelle; }
        public void setDateFinReelle(LocalDate dateFinReelle) { this.dateFinReelle = dateFinReelle; }

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }

        public String getQuarter() { return quarter; }
        public void setQuarter(String quarter) { this.quarter = quarter; }

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
    }
}