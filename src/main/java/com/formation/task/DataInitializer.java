package com.formation.task;

import com.formation.task.entities.User;
import com.formation.task.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {

            log.info("===== INITIALISATION DES UTILISATEURS =====");

            if (userRepository.findByUsername("admin").isEmpty()) {
                log.info("→ Création de l'utilisateur ADMIN");
                userRepository.save(new User(
                        null,
                        "admin",
                        "admin1@example.com",
                        encoder.encode("admin1231"),
                        "ROLE_ADMIN"
                ));
            } else {
                log.info("→ Admin existe déjà, insertion ignorée");
            }

            if (userRepository.findByUsername("user").isEmpty()) {
                log.info("→ Création de l'utilisateur USER");
                userRepository.save(new User(
                        null,
                        "user",
                        "user1@example.com",
                        encoder.encode("user1231"),
                        "ROLE_USER"
                ));
            } else {
                log.info("→ User existe déjà, insertion ignorée");
            }

            log.info("===== INITIALISATION TERMINÉE =====");
        };
    }
}
