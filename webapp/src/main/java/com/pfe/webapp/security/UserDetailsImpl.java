package com.pfe.webapp.security;

import com.pfe.webapp.entity.AffectationRole;
import com.pfe.webapp.entity.Compte;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String status;
    private String employeNom;  // ✅ إضافة اسم الموظف
    private String employePrenom; // ✅ إضافة لقب الموظف
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String password, String status,
                           String employeNom, String employePrenom,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
        this.employeNom = employeNom;
        this.employePrenom = employePrenom;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(Compte compte) {
        List<GrantedAuthority> authorities = compte.getRoles().stream()
                .filter(affectation -> {
                    LocalDate now = LocalDate.now();
                    return affectation.isActive() &&
                            (affectation.getDateDebut() == null || !affectation.getDateDebut().isAfter(now)) &&
                            (affectation.getDateFin() == null || !affectation.getDateFin().isBefore(now));
                })
                .map(affectation -> new SimpleGrantedAuthority("ROLE_" + affectation.getRole().getName()))
                .collect(Collectors.toList());

        String employeNom = compte.getEmploye() != null ? compte.getEmploye().getNom() : "";
        String employePrenom = compte.getEmploye() != null ? compte.getEmploye().getPrenom() : "";

        return new UserDetailsImpl(
                compte.getId(),
                compte.getUsername(),
                compte.getPassword(),
                compte.getStatus().name(),
                employeNom,
                employePrenom,
                authorities
        );
    }

    public List<String> getRoleNames() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public String getEmployeNom() { return employeNom; }
    public String getEmployePrenom() { return employePrenom; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return "ACTIVE".equals(status); }
}