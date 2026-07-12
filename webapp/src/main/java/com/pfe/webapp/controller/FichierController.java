// src/main/java/com/pfe/webapp/controller/FichierController.java
package com.pfe.webapp.controller;

import com.pfe.webapp.dto.FichierDTO;
import com.pfe.webapp.dto.FichierUploadRequestDTO;
import com.pfe.webapp.service.FichierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/fichiers")
@CrossOrigin(origins = "*")
public class FichierController {

    private static final Logger log = LoggerFactory.getLogger(FichierController.class);

    @Autowired
    private FichierService fichierService;

    // Upload file - requires authentication
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FichierDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("rapportId") Long rapportId,
            @RequestParam("titre") String titre,
            @RequestParam("type") String type) {

        log.info("📤 UPLOAD FILE - rapportId: {}, titre: {}, type: {}, fileName: {}, size: {}",
                rapportId, titre, type, file.getOriginalFilename(), file.getSize());

        FichierUploadRequestDTO request = new FichierUploadRequestDTO(titre,
                com.pfe.webapp.entity.TypeFichier.valueOf(type.toUpperCase()), rapportId);

        FichierDTO uploaded = fichierService.uploadFile(rapportId, file, request);
        log.info("✅ File uploaded successfully - id: {}", uploaded.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(uploaded);
    }

    // Get all fichiers for a rapport - requires authentication
    @GetMapping("/rapport/{rapportId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FichierDTO>> getFichiersByRapport(@PathVariable Long rapportId) {
        log.info("📋 GET FICHIERS BY RAPPORT - rapportId: {}", rapportId);
        List<FichierDTO> fichiers = fichierService.getFichiersByRapport(rapportId);
        log.info("✅ Found {} fichiers", fichiers.size());
        return ResponseEntity.ok(fichiers);
    }

    // Get fichiers by rapport and type - requires authentication
    @GetMapping("/rapport/{rapportId}/type/{type}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FichierDTO>> getFichiersByRapportAndType(
            @PathVariable Long rapportId,
            @PathVariable String type) {
        log.info("📋 GET FICHIERS BY RAPPORT AND TYPE - rapportId: {}, type: {}", rapportId, type);
        List<FichierDTO> fichiers = fichierService.getFichiersByRapportAndType(rapportId, type);
        log.info("✅ Found {} fichiers of type {}", fichiers.size(), type);
        return ResponseEntity.ok(fichiers);
    }

    // Get fichier by ID - requires authentication
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FichierDTO> getFichierById(@PathVariable Long id) {
        log.info("📋 GET FICHIER BY ID - id: {}", id);
        FichierDTO fichier = fichierService.getFichierById(id);
        log.info("✅ Found fichier - titre: {}, type: {}", fichier.getTitre(), fichier.getType());
        return ResponseEntity.ok(fichier);
    }

    // ✅ VIEW FILE - PERMIT ALL (for image/video preview) WITH DEBUGGING
    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> viewFile(@PathVariable Long id) {
        log.info("=========================================");
        log.info("🔍 VIEW FILE REQUEST - ID: {}", id);
        log.info("=========================================");

        // 🔍 DEBUG: Log request details
        log.info("📌 Request URI: {}", SecurityContextHolder.getContext());

        // 🔍 DEBUG: Check authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            log.info("🔐 Authentication: {}", auth.getName());
            log.info("🔐 Authorities: {}", auth.getAuthorities());
            log.info("🔐 Is Authenticated: {}", auth.isAuthenticated());
        } else {
            log.warn("⚠️ No authentication found in SecurityContext!");
        }

        try {
            log.info("📂 Attempting to download file with id: {}", id);

            // 🔍 DEBUG: Get file info first
            FichierDTO fichierInfo = fichierService.getFichierById(id);
            log.info("📄 File info - titre: {}, type: {}, chemin: {}, taille: {}",
                    fichierInfo.getTitre(),
                    fichierInfo.getType(),
                    fichierInfo.getChemin(),
                    fichierInfo.getTaille());

            // 🔍 DEBUG: Download resource
            log.info("📥 Downloading resource...");
            Resource resource = fichierService.downloadFile(id);

            if (resource == null) {
                log.error("❌ Resource is null for id: {}", id);
                return ResponseEntity.notFound().build();
            }

            if (!resource.exists()) {
                log.error("❌ Resource does not exist on disk for id: {}", id);
                log.error("   Path: {}", resource.getDescription());
                return ResponseEntity.notFound().build();
            }

            log.info("✅ Resource exists - filename: {}, size: {}",
                    resource.getFilename(),
                    resource.contentLength());

            // 🔍 DEBUG: Get content type
            String contentType = fichierService.getContentType(id);
            log.info("📋 Content-Type: {}", contentType);

            // 🔍 DEBUG: Get file name
            String fileName = fichierService.getFileName(id);
            log.info("📋 File name: {}", fileName);

            // 🔍 DEBUG: Log response headers
            log.info("📤 Sending response with headers:");
            log.info("   Content-Type: {}", contentType);
            log.info("   Content-Disposition: inline; filename=\"{}\"", fileName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (Exception e) {
            log.error("❌ ERROR viewing file with id: {}", id, e);
            log.error("   Error message: {}", e.getMessage());
            log.error("   Stack trace:");
            for (StackTraceElement element : e.getStackTrace()) {
                log.error("      {}", element);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            log.info("=========================================");
        }
    }

    // ✅ DOWNLOAD FILE - PERMIT ALL
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        log.info("🔽 DOWNLOAD FILE - ID: {}", id);

        try {
            Resource resource = fichierService.downloadFile(id);
            String filename = fichierService.getFileName(id);

            log.info("✅ Downloading file - filename: {}", filename);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            log.error("❌ Error downloading file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete fichier - requires authentication
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteFichier(@PathVariable Long id) {
        log.info("🗑️ DELETE FICHIER - id: {}", id);
        fichierService.deleteFichier(id);
        log.info("✅ File deleted successfully");
        return ResponseEntity.noContent().build();
    }

    // Delete all fichiers for a rapport - requires authentication
    @DeleteMapping("/rapport/{rapportId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAllFichiersByRapport(@PathVariable Long rapportId) {
        log.info("🗑️ DELETE ALL FICHIERS FOR RAPPORT - rapportId: {}", rapportId);
        fichierService.deleteAllFichiersByRapport(rapportId);
        log.info("✅ All files deleted for rapport: {}", rapportId);
        return ResponseEntity.noContent().build();
    }

    // Count fichiers by rapport - requires authentication
    @GetMapping("/rapport/{rapportId}/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countFichiersByRapport(@PathVariable Long rapportId) {
        log.info("📊 COUNT FICHIERS BY RAPPORT - rapportId: {}", rapportId);
        long count = fichierService.countFichiersByRapport(rapportId);
        log.info("✅ Count: {}", count);
        return ResponseEntity.ok(count);
    }
}