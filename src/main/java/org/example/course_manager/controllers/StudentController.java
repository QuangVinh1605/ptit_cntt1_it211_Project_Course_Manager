package org.example.course_manager.controllers;

import lombok.RequiredArgsConstructor;
import org.example.course_manager.dto.response.ApiResponse;
import org.example.course_manager.dto.response.SubmissionDto;
import org.example.course_manager.entity.User;
import org.example.course_manager.service.CourseService;
import org.example.course_manager.service.EnrollmentService;
import org.example.course_manager.service.SubmissionService;
import org.example.course_manager.service.UserService; // sẽ tạo
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
public class StudentController {

    private final EnrollmentService enrollmentService;
    private final SubmissionService submissionService;
    private final CourseService courseService;
    private final UserService userService;  // cần tạo

    @PostMapping("/courses/{courseId}/enroll")
    public ApiResponse enroll(@PathVariable Long courseId, Authentication auth) {
        User student = userService.findByUsername(auth.getName());
        var course = courseService.findById(courseId);
        enrollmentService.enrollStudent(student, course);
        return new ApiResponse(true, "Enrolled successfully");
    }

    @PostMapping("/submissions")
    public SubmissionDto submit(@RequestParam Long courseId,
                                @RequestParam String githubLink,
                                Authentication auth) {
        User student = userService.findByUsername(auth.getName());
        var course = courseService.findById(courseId);
        var submission = submissionService.submitGitHub(student, course, githubLink);
        return new SubmissionDto(submission.getId(), student.getId(), courseId,
                submission.getGithubLink(), null, submission.getStatus(),
                submission.getScore(), submission.getFeedback());
    }
}