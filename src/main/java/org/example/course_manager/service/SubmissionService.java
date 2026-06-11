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
    private final EnrollmentService enrollmentService;  // Thêm dependency

    public Submission submitGitHub(User student, Course course, String githubLink) {
        // Kiểm tra sinh viên đã đăng ký khóa học chưa
        if (!enrollmentService.isEnrolled(student, course)) {
            throw new RuntimeException("You must enroll in the course before submitting");
        }
        // Kiểm tra đã nộp bài chưa
        if (submissionRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("You have already submitted for this course");
        }
        Submission submission = new Submission();
        submission.setStudent(student);
        submission.setCourse(course);
        submission.setGithubLink(githubLink);
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }

    public Submission gradeSubmission(Long submissionId, Integer score, String feedback, String lecturerUsername) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        // Kiểm tra giảng viên có tồn tại và còn hoạt động (UserService đã kiểm tra active)
        User lecturer = userService.findByUsername(lecturerUsername);

        // Kiểm tra giảng viên có dạy khóa học này không
        if (!submission.getCourse().getLecturerId().equals(lecturer.getId())) {
            throw new RuntimeException("You are not the lecturer of this course");
        }

        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setStatus(SubmissionStatus.GRADED);
        return submissionRepository.save(submission);
    }
}