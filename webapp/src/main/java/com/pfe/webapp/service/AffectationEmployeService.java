package com.pfe.webapp.service;

import com.pfe.webapp.entity.AffectationEmploye;
import com.pfe.webapp.entity.Employe;
import com.pfe.webapp.entity.Mission;
import com.pfe.webapp.repository.AffectationEmployeRepository;
import com.pfe.webapp.repository.EmployeRepository;
import com.pfe.webapp.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class AffectationEmployeService {

    @Autowired
    private AffectationEmployeRepository affectationRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private MissionRepository missionRepository;

    public List<AffectationEmploye> getAllAffectations() {
        return affectationRepository.findAll();
    }

    public AffectationEmploye getAffectationById(Long id) {
        return affectationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Affectation non trouvée avec id: " + id));
    }

    public AffectationEmploye createAffectation(AffectationEmploye affectation) {
        // Vérifier que l'employé et la mission existent
        Employe employe = employeRepository.findById(affectation.getEmploye().getId())
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé"));
        Mission mission = missionRepository.findById(affectation.getMission().getId())
                .orElseThrow(() -> new EntityNotFoundException("Mission non trouvée"));

        affectation.setEmploye(employe);
        affectation.setMission(mission);

        return affectationRepository.save(affectation);
    }

    public AffectationEmploye updateAffectation(Long id, AffectationEmploye affectationDetails) {
        AffectationEmploye affectation = getAffectationById(id);
        affectation.setDateDebut(affectationDetails.getDateDebut());
        affectation.setDateFin(affectationDetails.getDateFin());
        if (affectationDetails.getEquipe() != null) {
            affectation.setEquipe(affectationDetails.getEquipe());
        }
        return affectationRepository.save(affectation);
    }

    public void deleteAffectation(Long id) {
        AffectationEmploye affectation = getAffectationById(id);
        affectationRepository.delete(affectation);
    }

    public List<AffectationEmploye> getAffectationsByEmploye(Long employeId) {
        return affectationRepository.findAll().stream()
                .filter(a -> a.getEmploye().getId().equals(employeId))
                .toList();
    }

    public List<AffectationEmploye> getAffectationsByMission(Long missionId) {
        return affectationRepository.findAll().stream()
                .filter(a -> a.getMission().getId().equals(missionId))
                .toList();
    }
}