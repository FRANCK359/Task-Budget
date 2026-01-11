package com.formation.task.controllers;

import com.formation.task.dto.LoginRequest;
import com.formation.task.dto.LoginResponse;
import com.formation.task.dto.UserRequest;
import com.formation.task.entities.User;
import com.formation.task.security.JwtService;
import com.formation.task.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserRequest request) {
        try {
            // Vérifier si l'utilisateur existe déjà
            if (userService.existsByUsername(request.getUsername())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("{\"message\": \"Nom d'utilisateur déjà utilisé\"}");
            }

            if (userService.existsByEmail(request.getEmail())) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("{\"message\": \"Email déjà utilisé\"}");
            }

            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(request.getPassword());
            newUser.setRole(request.getRole() != null ? request.getRole() : "ROLE_USER");

            User savedUser = userService.create(newUser);

            // Générer le token JWT après l'inscription
            String token = jwtService.generateToken(savedUser);

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setUsername(savedUser.getUsername());
            response.setRole(savedUser.getRole());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Erreur lors de l'inscription", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Erreur lors de l'inscription\"}");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                User user = userService.getByUsername(request.getUsername());
                String token = jwtService.generateToken(user);

                LoginResponse response = new LoginResponse();
                response.setToken(token);
                response.setUsername(user.getUsername());
                response.setRole(user.getRole());

                log.info("Utilisateur {} connecté avec succès", user.getUsername());
                return ResponseEntity.ok(response);
            }

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("{\"message\": \"Identifiants invalides\"}");

        } catch (AuthenticationException e) {
            log.warn("Échec de l'authentification pour l'utilisateur: {}", request.getUsername());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("{\"message\": \"Identifiants invalides\"}");
        } catch (Exception e) {
            log.error("Erreur lors de la connexion", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\": \"Erreur lors de la connexion\"}");
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(
                "{\"available\": " + !exists + ", \"message\": \"" +
                        (exists ? "Nom d'utilisateur déjà pris" : "Nom d'utilisateur disponible") + "\"}"
        );
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkEmail(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(
                "{\"available\": " + !exists + ", \"message\": \"" +
                        (exists ? "Email déjà utilisé" : "Email disponible") + "\"}"
        );
    }
}