package com.pfe.webapp.service.medical;

import com.pfe.webapp.dto.AntecedentsMedicalDTO;
import com.pfe.webapp.entity.AntecedentsMedical;
import com.pfe.webapp.entity.EtatMedical;
import com.pfe.webapp.repository.medical.AntecedentsMedicalRepository;
import com.pfe.webapp.repository.medical.EtatMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AntecedentsMedicalService {

    @Autowired
    private AntecedentsMedicalRepository antecedentsMedicalRepository;

    @Autowired
    private EtatMedicalRepository etatMedicalRepository;

    @Transactional
    public AntecedentsMedicalDTO createOrUpdateAntecedent(AntecedentsMedicalDTO dto) {
        AntecedentsMedical antecedent = dto.getId() != null
                ? antecedentsMedicalRepository.findById(dto.getId())
                .orElse(new AntecedentsMedical())
                : new AntecedentsMedical();

        antecedent.setNom(dto.getNom());
        antecedent.setDescription(dto.getDescription());
        antecedent.setDateDiagnostic(dto.getDateDiagnostic());

        if (dto.getEtatMedicalId() != null) {
            EtatMedical etatMedical = etatMedicalRepository.findById(dto.getEtatMedicalId())
                    .orElseThrow(() -> new RuntimeException("EtatMedical not found with id: " + dto.getEtatMedicalId()));
            antecedent.setEtatMedical(etatMedical);
        }

        antecedent = antecedentsMedicalRepository.save(antecedent);
        return convertToDTO(antecedent);
    }

    @Transactional(readOnly = true)
    public List<AntecedentsMedicalDTO> getAntecedentsByEtatMedicalId(Long etatMedicalId) {
        return antecedentsMedicalRepository.findByEtatMedicalId(etatMedicalId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AntecedentsMedicalDTO getAntecedentById(Long id) {
        AntecedentsMedical antecedent = antecedentsMedicalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Antecedent not found with id: " + id));
        return convertToDTO(antecedent);
    }

    @Transactional
    public void deleteAntecedent(Long id) {
        antecedentsMedicalRepository.deleteById(id);
    }

    @Transactional
    public void deleteByEtatMedicalId(Long etatMedicalId) {
        antecedentsMedicalRepository.deleteByEtatMedicalId(etatMedicalId);
    }

    private AntecedentsMedicalDTO convertToDTO(AntecedentsMedical antecedent) {
        return new AntecedentsMedicalDTO(
                antecedent.getId(),
                antecedent.getNom(),
                antecedent.getDescription(),
                antecedent.getDateDiagnostic(),
                antecedent.getEtatMedical() != null ? antecedent.getEtatMedical().getId() : null
        );
    }
}