// service/MotifService.java
package com.pfe.webapp.service;

import com.pfe.webapp.entity.ressource.Motif;
import com.pfe.webapp.repository.ressource.MotifRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MotifService {

    @Autowired
    private MotifRepository motifRepository;

    public List<Motif> getAllMotifs() {
        return motifRepository.findAll();
    }

    public Motif getMotifById(Long id) {
        return motifRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Motif not found"));
    }

    public Motif createMotif(Motif motif) {
        return motifRepository.save(motif);
    }

    public Motif updateMotif(Long id, Motif motifDetails) {
        Motif motif = getMotifById(id);
        motif.setCode(motifDetails.getCode());
        motif.setDescription(motifDetails.getDescription());
        return motifRepository.save(motif);
    }

    public void deleteMotif(Long id) {
        motifRepository.deleteById(id);
    }
}