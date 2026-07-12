// src/main/java/com/pfe/webapp/service/FichierServiceImpl.java
package com.pfe.webapp.service;

import com.pfe.webapp.dto.FichierDTO;
import com.pfe.webapp.dto.FichierUploadRequestDTO;
import com.pfe.webapp.entity.Fichier;
import com.pfe.webapp.entity.Rapport;
import com.pfe.webapp.entity.TypeFichier;
import com.pfe.webapp.repository.FichierRepository;
import com.pfe.webapp.repository.rapport.RapportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FichierServiceImpl implements FichierService {

    @Autowired
    private FichierRepository fichierRepository;

    @Autowired
    private RapportRepository rapportRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public FichierDTO uploadFile(Long rapportId, MultipartFile file, FichierUploadRequestDTO request) {
        Rapport rapport = rapportRepository.findById(rapportId)
                .orElseThrow(() -> new RuntimeException("Rapport not found with id: " + rapportId));

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(filename);

            Files.write(filePath, file.getBytes());

            Fichier fichier = new Fichier();
            fichier.setChemin(filePath.toString());
            fichier.setTitre(request.getTitre() != null ? request.getTitre() : originalFilename);
            fichier.setType(request.getType());
            fichier.setDateUpload(LocalDateTime.now());
            fichier.setTaille(file.getSize());
            fichier.setRapport(rapport);

            Fichier savedFichier = fichierRepository.save(fichier);
            return convertToDTO(savedFichier);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FichierDTO> getFichiersByRapport(Long rapportId) {
        return fichierRepository.findByRapportId(rapportId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FichierDTO> getFichiersByRapportAndType(Long rapportId, String type) {
        TypeFichier fichierType = TypeFichier.valueOf(type.toUpperCase());
        return fichierRepository.findByRapportIdAndType(rapportId, fichierType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FichierDTO getFichierById(Long id) {
        Fichier fichier = fichierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier not found with id: " + id));
        return convertToDTO(fichier);
    }

    // ✅ DOWNLOAD FILE
    @Override
    @Transactional(readOnly = true)
    public Resource downloadFile(Long id) {
        Fichier fichier = fichierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier not found with id: " + id));

        Path filePath = Paths.get(fichier.getChemin());
        Resource resource = new FileSystemResource(filePath.toFile());

        if (!resource.exists()) {
            throw new RuntimeException("File not found on disk: " + fichier.getChemin());
        }

        return resource;
    }

    // ✅ GET FILE NAME
    @Override
    @Transactional(readOnly = true)
    public String getFileName(Long id) {
        Fichier fichier = fichierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier not found with id: " + id));

        // Use the stored title, or extract from path
        if (fichier.getTitre() != null) {
            return fichier.getTitre();
        }

        Path path = Paths.get(fichier.getChemin());
        return path.getFileName().toString();
    }

    // ✅ GET CONTENT TYPE
    @Override
    @Transactional(readOnly = true)
    public String getContentType(Long id) {
        Fichier fichier = fichierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier not found with id: " + id));

        try {
            Path path = Paths.get(fichier.getChemin());
            String contentType = Files.probeContentType(path);
            if (contentType != null) {
                return contentType;
            }
        } catch (IOException e) {
            // Fallback to default
        }

        // Fallback based on type
        switch (fichier.getType()) {
            case IMAGE:
                return MediaType.IMAGE_JPEG_VALUE;
            case VIDEO:
                return MediaType.APPLICATION_OCTET_STREAM_VALUE;
            case DOCUMENT:
                return MediaType.APPLICATION_PDF_VALUE;
            default:
                return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }

    @Override
    @Transactional
    public void deleteFichier(Long id) {
        Fichier fichier = fichierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fichier not found with id: " + id));

        try {
            Path filePath = Paths.get(fichier.getChemin());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete physical file: " + e.getMessage());
        }

        fichierRepository.delete(fichier);
    }

    @Override
    @Transactional
    public void deleteAllFichiersByRapport(Long rapportId) {
        List<Fichier> fichiers = fichierRepository.findByRapportId(rapportId);
        for (Fichier fichier : fichiers) {
            try {
                Path filePath = Paths.get(fichier.getChemin());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                System.err.println("Failed to delete physical file: " + e.getMessage());
            }
        }
        fichierRepository.deleteByRapportId(rapportId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFichiersByRapport(Long rapportId) {
        return fichierRepository.countByRapportId(rapportId);
    }

    private FichierDTO convertToDTO(Fichier fichier) {
        FichierDTO dto = new FichierDTO();
        dto.setId(fichier.getId());
        dto.setChemin(fichier.getChemin());
        dto.setTitre(fichier.getTitre());
        dto.setType(fichier.getType());
        dto.setDateUpload(fichier.getDateUpload());
        dto.setTaille(fichier.getTaille());
        if (fichier.getRapport() != null) {
            dto.setRapportId(fichier.getRapport().getId());
        }
        return dto;
    }
}