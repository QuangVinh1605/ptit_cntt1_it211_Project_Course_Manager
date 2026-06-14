package org.example.course_manager.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.course_manager.dto.request.ChangePasswordRequest;
import org.example.course_manager.dto.response.ApiResponse;
import org.example.course_manager.dto.response.SubmissionDto;
import org.example.course_manager.entity.Course;
import org.example.course_manager.entity.Submission;
import org.example.course_manager.entity.User;
import org.example.course_manager.service.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/student")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
public class StudentController {

    private final EnrollmentService enrollmentService;
    private final SubmissionService submissionService;
    private final CourseService courseService;
    private final UserService userService;  // cần tạo
    private final CloudinaryService cloudinaryService;

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
    @PostMapping(value = "/submissions/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SubmissionDto submitWithFile(@RequestParam Long courseId,
                                        @RequestParam("file") MultipartFile file,
                                        Authentication auth) {
        User student = userService.findByUsername(auth.getName());
        Course course = courseService.findById(courseId);
        // Upload file lên cloud
        String fileUrl = cloudinaryService.uploadFile(file);
        Submission submission = submissionService.submitWithFile(student, course, fileUrl);
        return new SubmissionDto(submission.getId(), student.getId(), courseId,
                submission.getGithubLink(), submission.getReportUrl(),
                submission.getStatus(), submission.getScore(), submission.getFeedback());
    }
    @PostMapping("/change-password")
    public ApiResponse changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                      Authentication authentication) {
        String username = authentication.getName();
        userService.changePassword(username, request.getOldPassword(), request.getNewPassword());
        return new ApiResponse(true, "Đổi mật khẩu thành công");
    }
}