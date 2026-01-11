package com.formation.task.controllers;

import com.formation.task.dto.UserRequest;
import com.formation.task.dto.UserResponse;
import com.formation.task.entities.User;
import com.formation.task.mappers.UserMapper;
import com.formation.task.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

    // ----------------------------------------------------------
    // GET ALL → ADMIN UNIQUEMENT
    // ----------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(
                userService.getAll()
                        .stream()
                        .map(mapper::toResponse)
                        .toList()
        );
    }

    // ----------------------------------------------------------
    // GET ONE → ADMIN ou SELF
    // ----------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN') or #id == request.getAttribute('userId')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getOne(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(
                mapper.toResponse(userService.getById(id))
        );
    }

    // ----------------------------------------------------------
    // CREATE → ADMIN UNIQUEMENT
    // ----------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserRequest request) {
        User created = userService.create(mapper.toEntity(request));
        return ResponseEntity
                .created(URI.create("/users/" + created.getId()))
                .body(mapper.toResponse(created));
    }

    // ----------------------------------------------------------
    // UPDATE → ADMIN ou SELF
    // ----------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN') or #id == request.getAttribute('userId')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UserRequest requestDto,
            HttpServletRequest request
    ) {
        User updated = userService.update(id, mapper.toEntity(requestDto));
        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    // ----------------------------------------------------------
    // DELETE → ADMIN ou SELF
    // ----------------------------------------------------------
    @PreAuthorize("hasRole('ADMIN') or #id == request.getAttribute('userId')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
