package org.example.course_manager.repository;

import org.example.course_manager.entity.Course;
import org.example.course_manager.entity.Enrollment;
import org.example.course_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentAndCourse(User student, Course course);
}