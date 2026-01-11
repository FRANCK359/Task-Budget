package com.formation.task.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/test")
    public ResponseEntity<?> test(HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        return ResponseEntity.ok(
                "Accès sécurisé OK — userId = " + userId
        );
    }

    // Exemple d’endpoint sécurisé ADMIN uniquement
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminEndpoint() {
        return ResponseEntity.ok("Accès ADMIN validé !");
    }
}
