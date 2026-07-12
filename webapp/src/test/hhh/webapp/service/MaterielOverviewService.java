package com.pfe.webapp.service;

import com.pfe.webapp.dto.MaterielDetailDTO;
import com.pfe.webapp.dto.MaterielOverviewDTO;
import com.pfe.webapp.entity.materiel.*;
import com.pfe.webapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MaterielOverviewService {

    @Autowired
    private MaterielRepository materielRepository;

    @Autowired
    private MaterielImageRepository imageRepository;

    @Autowired
    private HistoriqueUtilisationRepository usageRepository;

    @Autowired
    private ReparationRepository reparationRepository;

    public MaterielOverviewDTO getMaterielOverview(Long materielId) {
        Materiel materiel = materielRepository.findById(materielId)
                .orElseThrow(() -> new RuntimeException("Materiel not found with id: " + materielId));

        MaterielOverviewDTO dto = new MaterielOverviewDTO(materiel);

        // Load images
        List<MaterielImage> images = imageRepository.findByMaterielIdMateriel(materielId);
        dto.setImages(images.stream()
                .map(MaterielOverviewDTO.ImageDTO::new)
                .collect(Collectors.toList()));

        // Load usage history
        List<HistoriqueUtilisation> usageHistory = usageRepository.findByMaterielId(materielId);
        dto.setTotalUsageCount(usageHistory.size());
        dto.setRecentUsageHistory(usageHistory.stream()
                .limit(5)
                .map(MaterielOverviewDTO.UsageHistoryDTO::new)
                .collect(Collectors.toList()));

        // Load repairs
        List<Reparation> repairs = reparationRepository.findByMaterielIdMateriel(materielId);
        dto.setTotalRepairCount(repairs.size());
        dto.setRecentRepairs(repairs.stream()
                .limit(5)
                .map(MaterielOverviewDTO.RepairDTO::new)
                .collect(Collectors.toList()));

        return dto;
    }

    public MaterielDetailDTO getMaterielDetails(Long materielId) {
        Materiel materiel = materielRepository.findById(materielId)
                .orElseThrow(() -> new RuntimeException("Materiel not found with id: " + materielId));

        MaterielDetailDTO dto = new MaterielDetailDTO(materiel);

        // Load all images
        List<MaterielImage> images = imageRepository.findByMaterielIdMateriel(materielId);
        dto.setImages(images.stream()
                .map(MaterielOverviewDTO.ImageDTO::new)
                .collect(Collectors.toList()));

        // Load all usage history
        List<HistoriqueUtilisation> usageHistory = usageRepository.findByMaterielId(materielId);
        dto.setTotalUsageCount(usageHistory.size());
        dto.setRecentUsageHistory(usageHistory.stream()
                .limit(5)
                .map(MaterielOverviewDTO.UsageHistoryDTO::new)
                .collect(Collectors.toList()));
        dto.setAllUsageHistory(usageHistory.stream()
                .map(MaterielOverviewDTO.UsageHistoryDTO::new)
                .collect(Collectors.toList()));

        // Load all repairs
        List<Reparation> repairs = reparationRepository.findByMaterielIdMateriel(materielId);
        dto.setTotalRepairCount(repairs.size());
        dto.setRecentRepairs(repairs.stream()
                .limit(5)
                .map(MaterielOverviewDTO.RepairDTO::new)
                .collect(Collectors.toList()));
        dto.setAllRepairs(repairs.stream()
                .map(MaterielOverviewDTO.RepairDTO::new)
                .collect(Collectors.toList()));

        return dto;
    }
}