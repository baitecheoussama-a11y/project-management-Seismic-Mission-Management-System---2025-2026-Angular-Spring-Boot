package com.pfe.webapp.repository;

import com.pfe.webapp.entity.Role;
import com.pfe.webapp.entity.TypeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    Optional<Role> findByType(TypeRole type);
}