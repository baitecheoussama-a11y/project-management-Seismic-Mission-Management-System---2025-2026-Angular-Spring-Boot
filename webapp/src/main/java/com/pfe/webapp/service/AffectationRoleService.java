package com.pfe.webapp.service;

import com.pfe.webapp.dto.AffectationRoleDTO;
import com.pfe.webapp.entity.AffectationRole;
import com.pfe.webapp.entity.Compte;
import com.pfe.webapp.entity.Role;
import com.pfe.webapp.repository.AffectationRoleRepository;
import com.pfe.webapp.repository.CompteRepository;
import com.pfe.webapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AffectationRoleService {

    @Autowired
    private AffectationRoleRepository affectationRoleRepository;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<AffectationRoleDTO> getRolesByCompteId(Long compteId) {
        return affectationRoleRepository.findByCompteId(compteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AffectationRoleDTO createAffectation(AffectationRoleDTO dto) {
        Compte compte = compteRepository.findById(dto.getCompteId())
                .orElseThrow(() -> new RuntimeException("Compte not found"));

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // التحقق من عدم وجود نفس الدور فعال حالياً
        boolean alreadyActive = affectationRoleRepository.findByCompteIdAndRoleIdAndActiveTrue(
                dto.getCompteId(), dto.getRoleId()).isPresent();

        if (alreadyActive && dto.isActive()) {
            throw new RuntimeException("This role is already active for this account");
        }

        AffectationRole ar = new AffectationRole();
        ar.setCompte(compte);
        ar.setRole(role);
        ar.setDateDebut(dto.getDateDebut() != null ? dto.getDateDebut() : LocalDate.now());
        ar.setDateFin(dto.getDateFin());
        ar.setActive(dto.isActive());

        ar = affectationRoleRepository.save(ar);
        return convertToDTO(ar);
    }

    @Transactional
    public AffectationRoleDTO updateAffectation(Long id, AffectationRoleDTO dto) {
        AffectationRole ar = affectationRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation not found"));

        ar.setDateDebut(dto.getDateDebut());
        ar.setDateFin(dto.getDateFin());
        ar.setActive(dto.isActive());

        if (dto.getRoleId() != null && !ar.getRole().getId().equals(dto.getRoleId())) {
            Role role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            ar.setRole(role);
        }

        ar = affectationRoleRepository.save(ar);
        return convertToDTO(ar);
    }

    @Transactional
    public void deleteAffectation(Long id) {
        affectationRoleRepository.deleteById(id);
    }

    @Transactional
    public AffectationRoleDTO toggleActive(Long id, boolean active) {
        AffectationRole ar = affectationRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation not found"));
        ar.setActive(active);
        ar = affectationRoleRepository.save(ar);
        return convertToDTO(ar);
    }

    private AffectationRoleDTO convertToDTO(AffectationRole ar) {
        AffectationRoleDTO dto = new AffectationRoleDTO();
        dto.setId(ar.getId());
        dto.setCompteId(ar.getCompte().getId());
        dto.setRoleId(ar.getRole().getId());
        dto.setRoleName(ar.getRole().getName());
        dto.setRoleType(ar.getRole().getType() != null ? ar.getRole().getType().toString() : "AUTRE");
        dto.setDateDebut(ar.getDateDebut());
        dto.setDateFin(ar.getDateFin());
        dto.setActive(ar.isActive());
        return dto;
    }
}