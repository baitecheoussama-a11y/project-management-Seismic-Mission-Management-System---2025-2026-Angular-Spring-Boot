// service/SiteService.java - corrected version
package com.pfe.webapp.service;

import com.pfe.webapp.dto.*;
import com.pfe.webapp.entity.*;
import com.pfe.webapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SiteService {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private WilayaRepository wilayaRepository;

    @Autowired
    private CoordonneeRepository coordonneeRepository;

    @Transactional(readOnly = true)
    public SiteResponseDTO getSiteByProjectId(Long projectId) {
        Optional<Site> siteOptional = siteRepository.findByProjectId(projectId);

        if (!siteOptional.isPresent()) return null;

        return convertToResponseDTO(siteOptional.get());
    }

    @Transactional
    public SiteResponseDTO createOrUpdateSite(SiteRequestDTO request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Wilaya wilaya = wilayaRepository.findById(request.getNumWilaya())
                .orElseThrow(() -> new RuntimeException("Wilaya not found"));

        Optional<Site> existingSite = siteRepository.findByProjectId(request.getProjectId());
        Site site;

        if (existingSite.isPresent()) {
            site = existingSite.get();
            // Update existing site
            site.setWilaya(wilaya);
            site.setSurface(request.getSurface());
        } else {
            // Create new site
            site = new Site();
            site.setProject(project);
            site.setWilaya(wilaya);
            site.setSurface(request.getSurface());
        }

        Site savedSite = siteRepository.save(site);

        // Handle coordinates - IMPORTANT: Clear and add, not replace the list
        if (request.getCoordonnees() != null) {
            // Clear existing coordinates
            if (savedSite.getCoordonnees() != null) {
                savedSite.getCoordonnees().clear();
            } else {
                savedSite.setCoordonnees(new ArrayList<>());
            }

            // Add new coordinates
            for (CoordonneeDTO coordDTO : request.getCoordonnees()) {
                Coordonnee coord = new Coordonnee();
                coord.setLatitude(coordDTO.getLatitude());
                coord.setLongitude(coordDTO.getLongitude());
                coord.setOrdre(coordDTO.getOrdre());
                coord.setSite(savedSite);
                savedSite.getCoordonnees().add(coord);
            }

            // Save again to persist the coordinates
            savedSite = siteRepository.save(savedSite);
        }

        return convertToResponseDTO(savedSite);
    }

    @Transactional
    public void deleteSite(Long projectId) {
        Optional<Site> siteOptional = siteRepository.findByProjectId(projectId);
        if (siteOptional.isPresent()) {
            Site site = siteOptional.get();
            // Clear coordinates first
            if (site.getCoordonnees() != null) {
                site.getCoordonnees().clear();
            }
            siteRepository.delete(site);
        }
    }

    private SiteResponseDTO convertToResponseDTO(Site site) {
        SiteResponseDTO dto = new SiteResponseDTO();
        dto.setId(site.getId());
        dto.setSurface(site.getSurface());

        if (site.getWilaya() != null) {
            WilayaDTO wilayaDTO = new WilayaDTO();
            wilayaDTO.setNumWilaya(site.getWilaya().getNumWilaya());
            wilayaDTO.setNom(site.getWilaya().getNom());
            wilayaDTO.setCenterLatitude(site.getWilaya().getCenterLatitude());
            wilayaDTO.setCenterLongitude(site.getWilaya().getCenterLongitude());
            dto.setWilaya(wilayaDTO);
        }

        if (site.getCoordonnees() != null && !site.getCoordonnees().isEmpty()) {
            List<CoordonneeDTO> coordDTOs = site.getCoordonnees().stream()
                    .map(coord -> {
                        CoordonneeDTO coordDTO = new CoordonneeDTO();
                        coordDTO.setLatitude(coord.getLatitude());
                        coordDTO.setLongitude(coord.getLongitude());
                        coordDTO.setOrdre(coord.getOrdre());
                        return coordDTO;
                    })
                    .collect(Collectors.toList());
            dto.setCoordonnees(coordDTOs);
        }

        return dto;
    }
}