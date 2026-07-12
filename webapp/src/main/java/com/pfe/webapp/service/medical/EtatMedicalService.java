package com.pfe.webapp.service.medical;

import com.pfe.webapp.dto.AntecedentsMedicalDTO;
import com.pfe.webapp.dto.EtatMedicalDTO;
import com.pfe.webapp.dto.EtatMedicalResponseDTO;
import com.pfe.webapp.entity.Employe;
import com.pfe.webapp.entity.EtatMedical;
import com.pfe.webapp.repository.EmployeRepository;
import com.pfe.webapp.repository.medical.EtatMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EtatMedicalService {

    @Autowired
    private EtatMedicalRepository etatMedicalRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private AntecedentsMedicalService antecedentsMedicalService;

    @Transactional
    public EtatMedicalResponseDTO createOrUpdateEtatMedical(EtatMedicalDTO dto) {
        Employe employe = employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employe not found with id: " + dto.getEmployeId()));

        EtatMedical etatMedical = etatMedicalRepository.findByEmployeId(dto.getEmployeId())
                .orElse(new EtatMedical());

        etatMedical.setGroupeSanguin(dto.getGroupeSanguin());
        etatMedical.setAllergies(dto.getAllergies());
        etatMedical.setVaccinations(dto.getVaccinations());
        etatMedical.setMedicationsActuelles(dto.getMedicationsActuelles());
        etatMedical.setMedecinTraitant(dto.getMedecinTraitant());
        etatMedical.setDerniereVisiteMedicale(dto.getDerniereVisiteMedicale());
        etatMedical.setEmploye(employe);

        etatMedical = etatMedicalRepository.save(etatMedical);

        // Handle antecedents if provided
        if (dto.getAntecedentsMedicaux() != null) {
            for (AntecedentsMedicalDTO antecedentDTO : dto.getAntecedentsMedicaux()) {
                antecedentDTO.setEtatMedicalId(etatMedical.getId());
                antecedentsMedicalService.createOrUpdateAntecedent(antecedentDTO);
            }
        }

        return convertToResponseDTO(etatMedical);
    }

    @Transactional(readOnly = true)
    public List<EtatMedicalResponseDTO> getAllEtatMedicals() {
        return etatMedicalRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EtatMedicalResponseDTO getEtatMedicalById(Long id) {
        EtatMedical etatMedical = etatMedicalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found with id: " + id));
        return convertToResponseDTO(etatMedical);
    }

    @Transactional(readOnly = true)
    public EtatMedicalResponseDTO getEtatMedicalByEmployeId(Long employeId) {
        EtatMedical etatMedical = etatMedicalRepository.findByEmployeId(employeId)
                .orElseThrow(() -> new RuntimeException("Medical record not found for employee id: " + employeId));
        return convertToResponseDTO(etatMedical);
    }

    @Transactional
    public void deleteEtatMedical(Long id) {
        // First delete all antecedents
        antecedentsMedicalService.deleteByEtatMedicalId(id);
        etatMedicalRepository.deleteById(id);
    }

    @Transactional
    public boolean existsByEmployeId(Long employeId) {
        return etatMedicalRepository.existsByEmployeId(employeId);
    }

    private EtatMedicalResponseDTO convertToResponseDTO(EtatMedical etatMedical) {
        EtatMedicalResponseDTO response = new EtatMedicalResponseDTO(
                etatMedical.getId(),
                etatMedical.getGroupeSanguin(),
                etatMedical.getAllergies(),
                etatMedical.getVaccinations(),
                etatMedical.getMedicationsActuelles(),
                etatMedical.getMedecinTraitant(),
                etatMedical.getDerniereVisiteMedicale(),
                etatMedical.getEmploye().getId(),
                etatMedical.getEmploye().getNom(),
                etatMedical.getEmploye().getPrenom()
        );

        if (etatMedical.getAntecedentsMedicaux() != null) {
            List<AntecedentsMedicalDTO> antecedentsDTOs = etatMedical.getAntecedentsMedicaux().stream()
                    .map(ant -> new AntecedentsMedicalDTO(
                            ant.getId(), ant.getNom(), ant.getDescription(),
                            ant.getDateDiagnostic(), ant.getEtatMedical().getId()))
                    .collect(Collectors.toList());
            response.setAntecedentsMedicaux(antecedentsDTOs);
        }

        if (etatMedical.getIncidents() != null) {
            response.setIncidentCount(etatMedical.getIncidents().size());
        }

        return response;
    }
}