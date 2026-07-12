// mongodb/service/RapportDetailsService.java
package com.pfe.webapp.mongodb.service;

import com.pfe.webapp.mongodb.document.RapportDetailsDocument;
import com.pfe.webapp.mongodb.repository.RapportDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RapportDetailsService {

    @Autowired
    private RapportDetailsRepository repository;

    public RapportDetailsDocument save(Long rapportId, Long projectId, Map<String, Object> details) {
        RapportDetailsDocument doc = new RapportDetailsDocument();
        doc.setRapportId(rapportId);
        doc.setProjectId(projectId);
        doc.setDetails(details);
        return repository.save(doc);
    }

    public RapportDetailsDocument save(RapportDetailsDocument doc) {
        return repository.save(doc);
    }

    public Optional<RapportDetailsDocument> getByRapportId(Long rapportId) {
        return repository.findByRapportId(rapportId);
    }

    public List<RapportDetailsDocument> getByProjectId(Long projectId) {
        return repository.findByProjectId(projectId);
    }

    public List<RapportDetailsDocument> getAll() {
        return repository.findAll();
    }

    public void deleteByRapportId(Long rapportId) {
        repository.findByRapportId(rapportId).ifPresent(repository::delete);
    }

    public boolean existsByRapportId(Long rapportId) {
        return repository.findByRapportId(rapportId).isPresent();
    }
}