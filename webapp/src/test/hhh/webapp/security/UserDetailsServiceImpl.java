package com.pfe.webapp.security;

import com.pfe.webapp.entity.Compte;
import com.pfe.webapp.repository.CompteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    CompteRepository compteRepository;

    @Override
    @Transactional  // ✅ مهم جداً لتحميل العلاقات (roles)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Compte compte = compteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // تأكد من تحميل roles
        compte.getRoles().size(); // force lazy loading

        return UserDetailsImpl.build(compte);
    }
}