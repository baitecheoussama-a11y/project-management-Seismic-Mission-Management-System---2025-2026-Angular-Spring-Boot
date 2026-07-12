package com.pfe.webapp.repository;

import com.pfe.webapp.entity.AffectationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AffectationRoleRepository extends JpaRepository<AffectationRole, Long> {

    List<AffectationRole> findByCompteId(Long compteId);

    Optional<AffectationRole> findByCompteIdAndRoleIdAndActiveTrue(Long compteId, Long roleId);

    // ✅ إصلاح: توزيع الأدوار
    @Query("SELECT ar.role.type, COUNT(ar) FROM AffectationRole ar GROUP BY ar.role.type")
    List<Object[]> countRolesDistribution();
}