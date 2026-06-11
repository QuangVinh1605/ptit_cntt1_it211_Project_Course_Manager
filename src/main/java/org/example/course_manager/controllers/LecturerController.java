package org.example.course_manager.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.course_manager.dto.request.GradeRequest;
import org.example.course_manager.dto.response.SubmissionDto;
import org.example.course_manager.service.SubmissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lecturer")
@PreAuthorize("hasRole('LECTURER')")
@RequiredArgsConstructor
public class LecturerController {

    private final SubmissionService submissionService;

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
}