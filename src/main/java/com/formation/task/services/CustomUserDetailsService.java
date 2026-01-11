package com.formation.task.services;

import com.formation.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Tentative de connexion avec un utilisateur inexistant: {}", username);
                    return new UsernameNotFoundException("Utilisateur non trouvé: " + username);
                });

        log.debug("Chargement de l'utilisateur '{}' avec le rôle '{}'", username, user.getRole());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // bcrypt ok
                .authorities(user.getRole()) // NB: ici on envoie ROLE_USER tel quel
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }


    private String cleanRole(String role) {
        if (role == null || role.isBlank()) return "USER";

        role = role.trim().toUpperCase();

        if (role.startsWith("ROLE_"))
            return role.substring(5);

        return role;
    }
}
