package org.example.course_manager.config;

import lombok.RequiredArgsConstructor;
import org.example.course_manager.constant.Role;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("Created admin user: admin/admin123");
        }

        if (!userRepository.existsByUsername("lecturer")) {
            User lecturer = new User();
            lecturer.setUsername("lecturer");
            lecturer.setEmail("lecturer@example.com");
            lecturer.setPassword(passwordEncoder.encode("lect123"));
            lecturer.setRole(Role.LECTURER);
            lecturer.setActive(true);
            userRepository.save(lecturer);
            System.out.println("Created lecturer user: lecturer/lect123");
        }

        if (!userRepository.existsByUsername("student")) {
            User student = new User();
            student.setUsername("student");
            student.setEmail("student@example.com");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setRole(Role.STUDENT);
            student.setActive(true);
            userRepository.save(student);
            System.out.println("Created student user: student/student123");
        }
    }
}