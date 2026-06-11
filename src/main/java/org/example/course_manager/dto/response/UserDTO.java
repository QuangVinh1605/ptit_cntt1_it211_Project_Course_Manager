package org.example.course_manager.dto.response;

import org.example.course_manager.constant.Role;

public record UserDTO(Long id, String username, String email, Role role, boolean active) {}