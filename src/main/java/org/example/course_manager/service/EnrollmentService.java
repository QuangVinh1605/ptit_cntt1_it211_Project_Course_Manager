package org.example.course_manager.service;

import lombok.RequiredArgsConstructor;
import org.example.course_manager.entity.Course;
import org.example.course_manager.entity.Enrollment;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public void enrollStudent(User student, Course course) {
        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("Already enrolled");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollmentRepository.save(enrollment);
    }

    public boolean isEnrolled(User student, Course course) {
        return enrollmentRepository.existsByStudentAndCourse(student, course);
    }
}