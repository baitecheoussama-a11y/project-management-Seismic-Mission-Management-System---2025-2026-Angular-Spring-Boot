// src/main/java/com/pfe/webapp/service/FichierService.java
package com.pfe.webapp.service;

import com.pfe.webapp.dto.FichierDTO;
import com.pfe.webapp.dto.FichierUploadRequestDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FichierService {
    FichierDTO uploadFile(Long rapportId, MultipartFile file, FichierUploadRequestDTO request);
    List<FichierDTO> getFichiersByRapport(Long rapportId);
    List<FichierDTO> getFichiersByRapportAndType(Long rapportId, String type);
    FichierDTO getFichierById(Long id);
    void deleteFichier(Long id);
    void deleteAllFichiersByRapport(Long rapportId);
    long countFichiersByRapport(Long rapportId);

    // ✅ NEW METHODS
    Resource downloadFile(Long id);
    String getFileName(Long id);
    String getContentType(Long id);
}