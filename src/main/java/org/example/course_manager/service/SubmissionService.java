package org.example.course_manager.service;

import lombok.RequiredArgsConstructor;
import org.example.course_manager.constant.SubmissionStatus;
import org.example.course_manager.entity.Course;
import org.example.course_manager.entity.Submission;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final CourseService courseService;
    private final UserService userService;
    private final EnrollmentService enrollmentService;

    public Submission submitGitHub(User student, Course course, String githubLink) {
        if (!enrollmentService.isEnrolled(student, course)) {
            throw new RuntimeException("You must enroll in the course before submitting");
        }
        if (submissionRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("You have already submitted for this course");
        }
        Submission submission = new Submission();
        submission.setStudent(student);
        submission.setCourse(course);
        submission.setGithubLink(githubLink);
        submission.setSubmittedAt(LocalDateTime.now());

        LocalDateTime deadline = course.getDeadline();
        if (deadline != null && LocalDateTime.now().isAfter(deadline)) {
            submission.setStatus(SubmissionStatus.LATE);
        } else {
            submission.setStatus(SubmissionStatus.SUBMITTED);
        }
        return submissionRepository.save(submission);
    }

    public Submission gradeSubmission(Long submissionId, Integer score, String feedback, String lecturerUsername) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        User lecturer = userService.findByUsername(lecturerUsername);

        if (!submission.getCourse().getLecturerId().equals(lecturer.getId())) {
            throw new RuntimeException("You are not the lecturer of this course");
        }

        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setStatus(SubmissionStatus.GRADED);
        return submissionRepository.save(submission);
    }

    public Submission submitWithFile(User student, Course course, String reportUrl) {
        if (!enrollmentService.isEnrolled(student, course)) {
            throw new RuntimeException("You must enroll in the course before submitting");
        }
        if (submissionRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("You have already submitted for this course");
        }
        Submission submission = new Submission();
        submission.setStudent(student);
        submission.setCourse(course);
        submission.setReportUrl(reportUrl);
        submission.setSubmittedAt(LocalDateTime.now());

        if (course.getDeadline() != null && LocalDateTime.now().isAfter(course.getDeadline())) {
            submission.setStatus(SubmissionStatus.LATE);
        } else {
            submission.setStatus(SubmissionStatus.SUBMITTED);
        }
        return submissionRepository.save(submission);
    }
}