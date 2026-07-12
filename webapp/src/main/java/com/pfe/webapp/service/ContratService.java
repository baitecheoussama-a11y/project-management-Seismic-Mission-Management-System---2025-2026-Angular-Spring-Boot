package com.pfe.webapp.service;

import com.pfe.webapp.dto.ContratDTO;
import com.pfe.webapp.dto.ContratResponseDTO;
import com.pfe.webapp.entity.Contrat;
import com.pfe.webapp.entity.Employe;
import com.pfe.webapp.repository.ContratRepository;
import com.pfe.webapp.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContratService {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Transactional
    public ContratResponseDTO createContrat(ContratDTO dto) {
        Employe employe = employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employe not found"));

        Contrat contrat = new Contrat();
        contrat.setType(dto.getType());
        contrat.setDateDebut(dto.getDateDebut());
        contrat.setDateFin(dto.getDateFin());
        contrat.setSalaire(dto.getSalaire());
        contrat.setDureeTravail(dto.getDureeTravail());
        contrat.setRegimeTravail(dto.getRegimeTravail());
        contrat.setEmploye(employe);

        contrat = contratRepository.save(contrat);
        return convertToResponseDTO(contrat);
    }

    @Transactional(readOnly = true)
    public List<ContratResponseDTO> getAllContrats() {
        return contratRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContratResponseDTO getContratById(Long id) {
        Contrat contrat = contratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat not found"));
        return convertToResponseDTO(contrat);
    }

    @Transactional(readOnly = true)
    public List<ContratResponseDTO> getContratsByEmploye(Long employeId) {
        return contratRepository.findByEmployeId(employeId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContratResponseDTO updateContrat(Long id, ContratDTO dto) {
        Contrat contrat = contratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat not found"));

        contrat.setType(dto.getType());
        contrat.setDateDebut(dto.getDateDebut());
        contrat.setDateFin(dto.getDateFin());
        contrat.setSalaire(dto.getSalaire());
        contrat.setDureeTravail(dto.getDureeTravail());
        contrat.setRegimeTravail(dto.getRegimeTravail());

        if (dto.getEmployeId() != null) {
            Employe employe = employeRepository.findById(dto.getEmployeId())
                    .orElseThrow(() -> new RuntimeException("Employe not found"));
            contrat.setEmploye(employe);
        }

        contrat = contratRepository.save(contrat);
        return convertToResponseDTO(contrat);
    }

    @Transactional
    public void deleteContrat(Long id) {
        contratRepository.deleteById(id);
    }

    private ContratResponseDTO convertToResponseDTO(Contrat contrat) {
        return new ContratResponseDTO(
                contrat.getId(),
                contrat.getType(),
                contrat.getDateDebut(),
                contrat.getDateFin(),
                contrat.getSalaire(),
                contrat.getDureeTravail(),
                contrat.getRegimeTravail(),
                contrat.getEmploye().getNom(),
                contrat.getEmploye().getPrenom(),
                contrat.getEmploye().getId()
        );
    }
}