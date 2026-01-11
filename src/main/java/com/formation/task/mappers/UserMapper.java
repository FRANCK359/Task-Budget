package com.formation.task.mappers;

import com.formation.task.dto.UserRequest;
import com.formation.task.dto.UserResponse;
import com.formation.task.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // -----------------------
    // UserRequest → User (pour create/update)
    // -----------------------
    public User toEntity(UserRequest request) {
        if (request == null) return null;

        return new User(
                null,
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
    }

    // -----------------------
    // User → UserResponse (pour retourner au client)
    // -----------------------
    public UserResponse toResponse(User user) {
        if (user == null) return null;

        return new UserResponse(user);
    }
}
