package org.example.course_manager.service;

import org.example.course_manager.entity.Course;
import org.example.course_manager.entity.Enrollment;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    void enrollStudent_ShouldSucceed_WhenNotEnrolled() {
        User student = new User();
        Course course = new Course();
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(new Enrollment());

        assertDoesNotThrow(() -> enrollmentService.enrollStudent(student, course));
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void enrollStudent_ShouldThrowException_WhenAlreadyEnrolled() {
        User student = new User();
        Course course = new Course();
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> enrollmentService.enrollStudent(student, course));
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void isEnrolled_ShouldReturnTrue_WhenExists() {
        User student = new User();
        Course course = new Course();
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);
        assertTrue(enrollmentService.isEnrolled(student, course));
    }

    @Test
    void isEnrolled_ShouldReturnFalse_WhenNotExists() {
        User student = new User();
        Course course = new Course();
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);
        assertFalse(enrollmentService.isEnrolled(student, course));
    }
}