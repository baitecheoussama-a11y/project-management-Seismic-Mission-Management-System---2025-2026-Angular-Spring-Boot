// service/ContexteService.java
package com.pfe.webapp.service;

import com.pfe.webapp.entity.ressource.Contexte;
import com.pfe.webapp.repository.ressource.ContexteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContexteService {

    @Autowired
    private ContexteRepository contexteRepository;

    public List<Contexte> getAllContextes() {
        return contexteRepository.findAll();
    }

    public Contexte getContexteById(Long id) {
        return contexteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contexte not found"));
    }

    public Contexte createContexte(Contexte contexte) {
        return contexteRepository.save(contexte);
    }

    public Contexte updateContexte(Long id, Contexte contexteDetails) {
        Contexte contexte = getContexteById(id);
        contexte.setTitre(contexteDetails.getTitre());
        contexte.setDescription(contexteDetails.getDescription());
        return contexteRepository.save(contexte);
    }

    public void deleteContexte(Long id) {
        contexteRepository.deleteById(id);
    }
}