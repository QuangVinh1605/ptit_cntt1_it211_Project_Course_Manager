package org.example.course_manager.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.course_manager.dto.request.GradeRequest;
import org.example.course_manager.dto.response.SubmissionDto;
import org.example.course_manager.entity.LectureMaterial;
import org.example.course_manager.service.CloudinaryService;
import org.example.course_manager.service.LecturerService;
import org.example.course_manager.service.SubmissionService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/lecturer")
@PreAuthorize("hasRole('LECTURER')")
@RequiredArgsConstructor
public class LecturerController {

    private final SubmissionService submissionService;
    private final CloudinaryService cloudinaryService;
    private final LecturerService lecturerService;

    @PutMapping("/submissions/{submissionId}/grade")
    public SubmissionDto grade(@PathVariable Long submissionId,
                               @Valid @RequestBody GradeRequest request,
                               Authentication authentication) {
        // Lấy username của giảng viên đang đăng nhập
        String lecturerUsername = authentication.getName();
        var submission = submissionService.gradeSubmission(submissionId, request.getScore(), request.getFeedback(), lecturerUsername);
        return new SubmissionDto(submission.getId(), submission.getStudent().getId(),
                submission.getCourse().getId(), submission.getGithubLink(),
                submission.getReportUrl(), submission.getStatus(),
                submission.getScore(), submission.getFeedback());
    }

    @PostMapping(value = "/courses/{courseId}/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public LectureMaterial uploadMaterial(@PathVariable Long courseId,
                                          @RequestParam("file") MultipartFile file,
                                          Authentication auth) {
        String lecturerUsername = auth.getName();
        String url = cloudinaryService.uploadFile(file);
        return lecturerService.uploadMaterial(courseId, file.getOriginalFilename(), url, lecturerUsername);
    }
}