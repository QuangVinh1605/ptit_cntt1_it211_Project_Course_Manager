package org.example.course_manager.service;

import lombok.RequiredArgsConstructor;
import org.example.course_manager.constant.SubmissionStatus;
import org.example.course_manager.entity.Course;
import org.example.course_manager.entity.Submission;
import org.example.course_manager.entity.User;
import org.example.course_manager.exceptions.DuplicateResourceException;
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
            throw new DuplicateResourceException("bạn phải đăng ký khoas hoc thì mới có thể nọp bài tập");
        }
        if (submissionRepository.existsByStudentAndCourse(student, course)) {
            throw new DuplicateResourceException("bạn đã nôppj bài này rồi");
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
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài nộp"));

        User lecturer = userService.findByUsername(lecturerUsername);

        Long courseLecturerId = submission.getCourse().getLecturerId();
        if (courseLecturerId == null || !courseLecturerId.equals(lecturer.getId())) {
            throw new RuntimeException("Bạn không phải giảng viên của khóa học này hoặc khóa học chưa có giảng viên");
        }

        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setStatus(SubmissionStatus.GRADED);
        return submissionRepository.save(submission);
    }
    public Submission submitWithFile(User student, Course course, String reportUrl) {
        if (!enrollmentService.isEnrolled(student, course)) {
            throw new RuntimeException("bạn phải đăng ký kháo hcoj trước khi nộp bài");
        }
        if (submissionRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("bạn đã nộp bài này rồi");
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