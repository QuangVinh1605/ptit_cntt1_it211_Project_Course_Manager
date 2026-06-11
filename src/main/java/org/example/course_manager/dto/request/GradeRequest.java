package org.example.course_manager.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GradeRequest {
    @Min(0) @Max(100)
    private Integer score;
    private String feedback;
}