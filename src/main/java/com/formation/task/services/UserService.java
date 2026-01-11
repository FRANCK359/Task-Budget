package com.formation.task.services;

import com.formation.task.entities.User;
import com.formation.task.exceptions.BusinessException;
import com.formation.task.exceptions.NotFoundException;
import com.formation.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // --------------------------------------------
    // BASIC RETRIEVAL
    // --------------------------------------------
    public List<User> getAll() {   // FIX: List<User>>
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Utilisateur non trouvé avec l'ID: " + id)
                );
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new NotFoundException("Utilisateur non trouvé : " + username)
                );
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // --------------------------------------------
    // CREATE
    // --------------------------------------------
    @Transactional
    public User create(User user) {

        validateUserForCreate(user);

        if (existsByUsername(user.getUsername()))
            throw new BusinessException("Ce nom d'utilisateur est déjà utilisé");

        if (existsByEmail(user.getEmail()))
            throw new BusinessException("Cet email est déjà utilisé");

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null || user.getRole().isBlank())
            user.setRole("ROLE_USER");

        log.info("Création de l'utilisateur : {}", user.getUsername());
        return userRepository.save(user);
    }

    private void validateUserForCreate(User user) {
        if (user == null)
            throw new BusinessException("Utilisateur invalide");

        if (isBlank(user.getUsername()))
            throw new BusinessException("Le nom d'utilisateur est requis");

        if (isBlank(user.getEmail()))
            throw new BusinessException("L'email est requis");

        if (isBlank(user.getPassword()))
            throw new BusinessException("Le mot de passe est requis");
    }

    // --------------------------------------------
    // UPDATE
    // --------------------------------------------
    @Transactional
    public User update(Long id, User newData) {

        User user = getById(id);

        updateUsername(id, newData, user);
        updateEmail(id, newData, user);
        updatePassword(newData, user);

        if (newData.getRole() != null && !newData.getRole().isBlank())
            user.setRole(newData.getRole());

        log.info("Utilisateur {} mis à jour", user.getUsername());
        return userRepository.save(user);
    }

    private void updateUsername(Long id, User newData, User user) {
        if (newData.getUsername() != null && !newData.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(newData.getUsername())) {
                User existing = userRepository.findByUsername(newData.getUsername())
                        .orElse(null);

                if (existing != null && !existing.getId().equals(id))
                    throw new BusinessException("Ce nom d'utilisateur est déjà utilisé");
            }

            user.setUsername(newData.getUsername());
        }
    }

    private void updateEmail(Long id, User newData, User user) {
        if (newData.getEmail() != null && !newData.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmail(newData.getEmail())) {
                User existing = userRepository.findByEmail(newData.getEmail())
                        .orElse(null);

                if (existing != null && !existing.getId().equals(id))
                    throw new BusinessException("Cet email est déjà utilisé");
            }

            user.setEmail(newData.getEmail());
        }
    }

    private void updatePassword(User newData, User user) {
        if (newData.getPassword() != null && !newData.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(newData.getPassword()));
        }
    }

    // --------------------------------------------
    // DELETE
    // --------------------------------------------
    @Transactional
    public void delete(Long id) {

        User user = getById(id);

        if (user.getMonthlyBudgets() != null && !user.getMonthlyBudgets().isEmpty())
            log.warn("Suppression de l'utilisateur {} avec budgets associés", user.getUsername());

        if (user.getTasks() != null && !user.getTasks().isEmpty())
            log.warn("Suppression de l'utilisateur {} avec tâches associées", user.getUsername());

        userRepository.delete(user);
        log.info("Utilisateur {} supprimé", user.getUsername());
    }

    // --------------------------------------------
    // UTILS
    // --------------------------------------------
    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
