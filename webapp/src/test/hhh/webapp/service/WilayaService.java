// service/WilayaService.java
package com.pfe.webapp.service;

import com.pfe.webapp.dto.WilayaDTO;
import com.pfe.webapp.entity.Wilaya;
import com.pfe.webapp.repository.WilayaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WilayaService {

    @Autowired
    private WilayaRepository wilayaRepository;

    // Get all wilayas as DTOs
    @Transactional(readOnly = true)
    public List<WilayaDTO> getAllWilayas() {
        return wilayaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get wilaya by ID as DTO
    @Transactional(readOnly = true)
    public WilayaDTO getWilayaById(Integer numWilaya) {
        Wilaya wilaya = wilayaRepository.findById(numWilaya)
                .orElseThrow(() -> new RuntimeException("Wilaya not found with id: " + numWilaya));
        return convertToDTO(wilaya);
    }

    // Get wilaya by name as DTO
    @Transactional(readOnly = true)
    public WilayaDTO getWilayaByNom(String nom) {
        Wilaya wilaya = wilayaRepository.findByNom(nom)
                .orElseThrow(() -> new RuntimeException("Wilaya not found with name: " + nom));
        return convertToDTO(wilaya);
    }

    // Get wilaya entity by ID (for admin/internal use)
    @Transactional(readOnly = true)
    public Wilaya getWilayaEntityById(Integer numWilaya) {
        return wilayaRepository.findById(numWilaya)
                .orElseThrow(() -> new RuntimeException("Wilaya not found with id: " + numWilaya));
    }

    // Create new wilaya
    @Transactional
    public WilayaDTO createWilaya(WilayaDTO wilayaDTO) {
        // Check if wilaya already exists
        if (wilayaRepository.existsById(wilayaDTO.getNumWilaya())) {
            throw new RuntimeException("Wilaya with id " + wilayaDTO.getNumWilaya() + " already exists");
        }

        Wilaya wilaya = new Wilaya();
        wilaya.setNumWilaya(wilayaDTO.getNumWilaya());
        wilaya.setNom(wilayaDTO.getNom());
        wilaya.setCenterLatitude(wilayaDTO.getCenterLatitude());
        wilaya.setCenterLongitude(wilayaDTO.getCenterLongitude());

        Wilaya saved = wilayaRepository.save(wilaya);
        return convertToDTO(saved);
    }

    // Update existing wilaya
    @Transactional
    public WilayaDTO updateWilaya(Integer numWilaya, WilayaDTO wilayaDTO) {
        Wilaya wilaya = wilayaRepository.findById(numWilaya)
                .orElseThrow(() -> new RuntimeException("Wilaya not found with id: " + numWilaya));

        wilaya.setNom(wilayaDTO.getNom());
        wilaya.setCenterLatitude(wilayaDTO.getCenterLatitude());
        wilaya.setCenterLongitude(wilayaDTO.getCenterLongitude());

        Wilaya updated = wilayaRepository.save(wilaya);
        return convertToDTO(updated);
    }

    // Delete wilaya
    @Transactional
    public void deleteWilaya(Integer numWilaya) {
        Wilaya wilaya = wilayaRepository.findById(numWilaya)
                .orElseThrow(() -> new RuntimeException("Wilaya not found with id: " + numWilaya));

        // Check if wilaya has associated sites
        if (wilaya.getSites() != null && !wilaya.getSites().isEmpty()) {
            throw new RuntimeException("Cannot delete wilaya that has associated sites");
        }

        wilayaRepository.delete(wilaya);
    }

    // Convert Entity to DTO
    private WilayaDTO convertToDTO(Wilaya wilaya) {
        WilayaDTO dto = new WilayaDTO();
        dto.setNumWilaya(wilaya.getNumWilaya());
        dto.setNom(wilaya.getNom());
        dto.setCenterLatitude(wilaya.getCenterLatitude());
        dto.setCenterLongitude(wilaya.getCenterLongitude());
        return dto;
    }
}