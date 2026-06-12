package org.example.course_manager.service;

import lombok.RequiredArgsConstructor;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("khong tim thay user: " + username));
        if (!user.isActive()) {
            throw new RuntimeException("User account dang  khongkich hoat");
        }
        return user;
    }
}