package org.example.course_manager.controllers;

import lombok.RequiredArgsConstructor;
import org.example.course_manager.constant.Role;
import org.example.course_manager.dto.response.UserDTO;
import org.example.course_manager.entity.Course;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.CourseRepository;
import org.example.course_manager.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public Page<UserDTO> getUsers(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage;
        if (search != null && !search.isEmpty()) {
            userPage = userRepository.findByUsernameContainingOrEmailContaining(search, search, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return userPage.map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isActive()));
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@RequestBody User user) {
        // Nếu client không gửi role, mặc định là STUDENT
        if (user.getRole() == null) {
            user.setRole(Role.STUDENT);
        }
        // Mã hóa mật khẩu mặc định
        user.setPassword(passwordEncoder.encode("mat khau mac dinh"));
        return convertToDTO(userRepository.save(user));
    }

    @GetMapping("/courses")
    public Page<Course> getCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isActive());
    }

    @PostMapping("/courses")
    @ResponseStatus(HttpStatus.CREATED)
    public Course createCourse(@RequestBody Course course) {
        return courseRepository.save(course);
    }

    @PutMapping("/courses/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody Course course) {
        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        existing.setName(course.getName());
        existing.setDescription(course.getDescription());
        existing.setLecturerId(course.getLecturerId());
        return courseRepository.save(existing);
    }

    @DeleteMapping("/courses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);
    }
}