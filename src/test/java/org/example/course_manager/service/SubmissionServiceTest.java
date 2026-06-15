package org.example.course_manager.service;

import org.example.course_manager.constant.SubmissionStatus;
import org.example.course_manager.entity.Course;
import org.example.course_manager.entity.Submission;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private CourseService courseService;
    @Mock
    private UserService userService;
    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private SubmissionService submissionService;

    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        student = new User();
        student.setId(1L);
        student.setUsername("student1");

        course = new Course();
        course.setId(10L);
        course.setName("Test Course");
        course.setDeadline(LocalDateTime.now().plusDays(1));
    }

    @Test
    void submitGitHub_ShouldSucceed_WhenEnrolledAndNotSubmittedYet() {
        when(enrollmentService.isEnrolled(student, course)).thenReturn(true);
        when(submissionRepository.existsByStudentAndCourse(student, course)).thenReturn(false);
        when(submissionRepository.save(any(Submission.class))).thenAnswer(inv -> inv.getArgument(0));

        Submission sub = submissionService.submitGitHub(student, course, "https://github.com/test/repo");

        assertNotNull(sub);
        assertEquals("https://github.com/test/repo", sub.getGithubLink());
        assertEquals(SubmissionStatus.SUBMITTED, sub.getStatus());
        assertNotNull(sub.getSubmittedAt());
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    void submitGitHub_ShouldThrowException_WhenNotEnrolled() {
        when(enrollmentService.isEnrolled(student, course)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> submissionService.submitGitHub(student, course, "link"));
        verify(submissionRepository, never()).save(any());
    }

    @Test
    void submitGitHub_ShouldThrowException_WhenAlreadySubmitted() {
        when(enrollmentService.isEnrolled(student, course)).thenReturn(true);
        when(submissionRepository.existsByStudentAndCourse(student, course)).thenReturn(true);
        assertThrows(RuntimeException.class, () -> submissionService.submitGitHub(student, course, "link"));
    }

    @Test
    void gradeSubmission_ShouldSucceed_WhenLecturerIsOwner() {
        User lecturer = new User();
        lecturer.setId(2L);
        course.setLecturerId(2L);

        Submission submission = new Submission();
        submission.setId(100L);
        submission.setStudent(student);
        submission.setCourse(course);
        submission.setStatus(SubmissionStatus.SUBMITTED);

        when(submissionRepository.findById(100L)).thenReturn(Optional.of(submission));
        when(userService.findByUsername("lecturer1")).thenReturn(lecturer);
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);

        Submission graded = submissionService.gradeSubmission(100L, 85, "Good job!", "lecturer1");

        assertEquals(85, graded.getScore());
        assertEquals("Good job!", graded.getFeedback());
        assertEquals(SubmissionStatus.GRADED, graded.getStatus());
        verify(submissionRepository).save(submission);
    }

    @Test
    void gradeSubmission_ShouldThrowException_WhenLecturerNotOwner() {
        User lecturer = new User();
        lecturer.setId(3L);
        course.setLecturerId(2L); // khác lecturer.getId()
        Submission submission = new Submission();
        submission.setId(100L);
        submission.setCourse(course);

        when(submissionRepository.findById(100L)).thenReturn(Optional.of(submission));
        when(userService.findByUsername("lecturer1")).thenReturn(lecturer);

        assertThrows(RuntimeException.class, () -> submissionService.gradeSubmission(100L, 85, "feedback", "lecturer1"));
        verify(submissionRepository, never()).save(any());
    }
}