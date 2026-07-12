package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Compte;
import com.pfe.webapp.entity.TypeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {

    Optional<Compte> findByUsername(String username);

    Optional<Compte> findByEmployeId(Long employeId);

    // ✅ إصلاح: استخدم StatusCompte.ACTIVE مباشرة
    @Query("SELECT COUNT(c) FROM Compte c WHERE c.status = 'ACTIVE'")
    long countByStatus();

    // بديل: استخدم enum مباشرة
    long countByStatus(com.pfe.webapp.entity.StatusCompte status);

    // ✅ NEW: Find comptes by role name
    @Query("SELECT c FROM Compte c JOIN c.roles r WHERE r.role.name = :roleName")
    List<Compte> findComptesByRole(@Param("roleName") String roleName);

    // ✅ NEW: Find comptes by role type
    @Query("SELECT c FROM Compte c JOIN c.roles r WHERE r.role.type = :roleType")
    List<Compte> findComptesByRoleType(@Param("roleType") TypeRole roleType);

    // ✅ NEW: Find comptes by role name and status
    @Query("SELECT c FROM Compte c JOIN c.roles r WHERE r.role.name = :roleName AND c.status = 'ACTIVE'")
    List<Compte> findActiveComptesByRole(@Param("roleName") String roleName);

    // ✅ NEW: Find comptes by role type and status
    @Query("SELECT c FROM Compte c JOIN c.roles r WHERE r.role.type = :roleType AND c.status = 'ACTIVE'")
    List<Compte> findActiveComptesByRoleType(@Param("roleType") TypeRole roleType);

    // ✅ NEW: Find comptes with specific role name (alternative using JPQL with IN)
    @Query("SELECT c FROM Compte c WHERE EXISTS (SELECT 1 FROM AffectationRole ar WHERE ar.compte = c AND ar.role.name = :roleName)")
    List<Compte> findComptesWithRole(@Param("roleName") String roleName);

    // ✅ NEW: Check if a compte has a specific role
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Compte c JOIN c.roles r WHERE c.id = :compteId AND r.role.name = :roleName")
    boolean hasRole(@Param("compteId") Long compteId, @Param("roleName") String roleName);

    // ✅ NEW: Get all admin comptes (ADMINISTRATEUR role)
    default List<Compte> findAllAdmins() {
        return findComptesByRoleType(TypeRole.ADMINISTRATEUR);
    }

    // ✅ NEW: Get all chef mission comptes
    default List<Compte> findAllChefMission() {
        return findComptesByRoleType(TypeRole.CHEF_MISSION);
    }

    // ✅ NEW: Get all chef terrain comptes
    default List<Compte> findAllChefTerrain() {
        return findComptesByRoleType(TypeRole.CHEF_TERRAIN);
    }

    // ✅ NEW: Get all directeur comptes
    default List<Compte> findAllDirecteurs() {
        return findComptesByRoleType(TypeRole.DIRECTEUR);
    }

    // ✅ NEW: Get all gestionnaire comptes
    default List<Compte> findAllGestionnaires() {
        return findComptesByRoleType(TypeRole.Gestionnaire);
    }
}