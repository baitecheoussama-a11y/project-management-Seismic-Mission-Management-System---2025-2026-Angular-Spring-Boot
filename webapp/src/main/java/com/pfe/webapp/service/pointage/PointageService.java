package com.pfe.webapp.service.pointage;

import com.pfe.webapp.dto.pointage.PointageRequestDTO;
import com.pfe.webapp.dto.pointage.PointageResponseDTO;
import com.pfe.webapp.dto.pointage.PointageStatsDTO;
import com.pfe.webapp.entity.Employe;
import com.pfe.webapp.entity.Pointage;
import com.pfe.webapp.entity.StatusPointage;
import com.pfe.webapp.repository.EmployeRepository;
import com.pfe.webapp.repository.PointageRepository;
import com.pfe.webapp.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PointageService {

    @Autowired
    private PointageRepository pointageRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private NotificationService notificationService;

    // ========== CREATE ==========

    @Transactional
    public PointageResponseDTO createPointage(PointageRequestDTO request) {
        // Validate employee exists
        Employe employe = employeRepository.findById(request.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employe not found with id: " + request.getEmployeId()));

        // Check if pointage already exists for this employee on this date
        if (pointageRepository.existsByEmployeIdAndDate(request.getEmployeId(), request.getDatePointage())) {
            throw new RuntimeException("Pointage already exists for this employee on this date");
        }

        // Parse status
        StatusPointage status = StatusPointage.fromString(request.getStatus());

        // Create pointage
        Pointage pointage = new Pointage();
        pointage.setDatePointage(request.getDatePointage());
        pointage.setStatus(status);
        pointage.setMotifAbsent(request.getMotifAbsent());
        pointage.setRemarque(request.getRemarque());
        pointage.setEmploye(employe);

        Pointage saved = pointageRepository.save(pointage);

        // Send notification if absent or late
        if (status == StatusPointage.ABSENT || status == StatusPointage.RETARD) {
            sendAttendanceNotification(employe, status, saved);
        }

        return convertToDTO(saved);
    }

    // ========== UPDATE ==========

    @Transactional
    public PointageResponseDTO updatePointage(Long id, PointageRequestDTO request) {
        Pointage pointage = pointageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pointage not found with id: " + id));

        // Update fields
        if (request.getStatus() != null) {
            pointage.setStatus(StatusPointage.fromString(request.getStatus()));
        }
        if (request.getMotifAbsent() != null) {
            pointage.setMotifAbsent(request.getMotifAbsent());
        }
        if (request.getRemarque() != null) {
            pointage.setRemarque(request.getRemarque());
        }

        Pointage updated = pointageRepository.save(pointage);
        return convertToDTO(updated);
    }

    // ========== DELETE ==========

    @Transactional
    public void deletePointage(Long id) {
        if (!pointageRepository.existsById(id)) {
            throw new RuntimeException("Pointage not found with id: " + id);
        }
        pointageRepository.deleteById(id);
    }

    // ========== GET BY ID ==========

    public PointageResponseDTO getPointageById(Long id) {
        Pointage pointage = pointageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pointage not found with id: " + id));
        return convertToDTO(pointage);
    }

    // ========== GET BY DATE ==========

    public List<PointageResponseDTO> getPointagesByDate(LocalDate date) {
        return pointageRepository.findByDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== GET BY EMPLOYEE ==========

    public List<PointageResponseDTO> getPointagesByEmploye(Long employeId) {
        return pointageRepository.findByEmployeId(employeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== GET BY EMPLOYEE AND DATE ==========

    public PointageResponseDTO getPointageByEmployeAndDate(Long employeId, LocalDate date) {
        Pointage pointage = pointageRepository.findByEmployeIdAndDate(employeId, date)
                .orElseThrow(() -> new RuntimeException("Pointage not found for employee on this date"));
        return convertToDTO(pointage);
    }

    // ========== GET BY DATE RANGE ==========

    public List<PointageResponseDTO> getPointagesByDateRange(LocalDate startDate, LocalDate endDate) {
        return pointageRepository.findByDateRange(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== GET TODAY'S POINTAGES ==========

    public List<PointageResponseDTO> getTodaysPointages() {
        return pointageRepository.findTodaysPointages().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ========== GET STATS ==========

    public PointageStatsDTO getStatsByDate(LocalDate date) {
        List<Pointage> pointages = pointageRepository.findByDate(date);
        List<Employe> allEmployees = employeRepository.findAll();

        PointageStatsDTO stats = new PointageStatsDTO();
        stats.setTotalEmployees(allEmployees.size());

        long present = 0, absent = 0, late = 0, onLeave = 0, onMission = 0;

        for (Pointage p : pointages) {
            switch (p.getStatus()) {
                case PRESENT:
                    present++;
                    break;
                case ABSENT:
                    absent++;
                    break;
                case RETARD:
                    late++;
                    break;
                case CONGE:
                    onLeave++;
                    break;
                case MISSION:
                    onMission++;
                    break;
            }
        }

        stats.setPresent(present);
        stats.setAbsent(absent);
        stats.setLate(late);
        stats.setOnLeave(onLeave);
        stats.setOnMission(onMission);
        stats.setNotRecorded(allEmployees.size() - pointages.size());

        return stats;
    }

    // ========== MARK ALL PRESENT ==========

    @Transactional
    public void markAllPresent(LocalDate date) {
        List<Employe> allEmployees = employeRepository.findAll();

        for (Employe employe : allEmployees) {
            if (!pointageRepository.existsByEmployeIdAndDate(employe.getId(), date)) {
                Pointage pointage = new Pointage();
                pointage.setDatePointage(date);
                pointage.setStatus(StatusPointage.PRESENT);
                pointage.setEmploye(employe);
                pointageRepository.save(pointage);
            }
        }
    }

    // ========== GET STATUS OPTIONS ==========

    public List<StatusPointage> getStatusOptions() {
        return List.of(StatusPointage.values());
    }

    // ========== NOTIFICATION HELPERS ==========

    private void sendAttendanceNotification(Employe employe, StatusPointage status, Pointage pointage) {
        String title = "Attendance Alert";
        String message = String.format("%s %s is marked as %s on %s",
                employe.getPrenom(),
                employe.getNom(),
                status.getLabel(),
                pointage.getDatePointage().toString());

        if (pointage.getMotifAbsent() != null && !pointage.getMotifAbsent().isEmpty()) {
            message += " (Reason: " + pointage.getMotifAbsent() + ")";
        }

        // You can implement notification logic here
        // For now, just log it
        System.out.println("ATTENDANCE NOTIFICATION: " + message);
    }

    // ========== CONVERTERS ==========

    private PointageResponseDTO convertToDTO(Pointage pointage) {
        PointageResponseDTO dto = new PointageResponseDTO();
        dto.setId(pointage.getId());
        dto.setDatePointage(pointage.getDatePointage());
        dto.setStatus(pointage.getStatus());
        dto.setMotifAbsent(pointage.getMotifAbsent());
        dto.setRemarque(pointage.getRemarque());

        if (pointage.getEmploye() != null) {
            dto.setEmployeId(pointage.getEmploye().getId());
            dto.setEmployeNom(pointage.getEmploye().getNom());
            dto.setEmployePrenom(pointage.getEmploye().getPrenom());
        }

        if (pointage.getStatus() != null) {
            dto.setStatusLabel(pointage.getStatus().getLabel());
            dto.setStatusColor(pointage.getStatus().getColor());
        }

        return dto;
    }
}