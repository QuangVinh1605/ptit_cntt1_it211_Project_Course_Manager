package org.example.course_manager.dto.request;

import lombok.Data;
import org.example.course_manager.constant.Role;

@Data
public class UserUpdateRequest {
    private String username;
    private String email;
    private Role role;
    private boolean active;
}