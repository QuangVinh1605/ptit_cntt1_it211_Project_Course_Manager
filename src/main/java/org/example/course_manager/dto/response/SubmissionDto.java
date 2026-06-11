package org.example.course_manager.dto.response;

import org.example.course_manager.constant.SubmissionStatus;

public record SubmissionDto(Long id, Long studentId, Long courseId,
                            String githubLink, String reportUrl,
                            SubmissionStatus status, Integer score, String feedback) {}