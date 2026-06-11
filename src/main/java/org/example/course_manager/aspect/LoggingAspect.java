package org.example.course_manager.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @AfterReturning(pointcut = "execution(* org.example.course_manager.service.SubmissionService.gradeSubmission(..))")
    public void logGradeSubmission(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Long submissionId = (Long) args[0];
        Integer score = (Integer) args[1];
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String lecturer = auth != null ? auth.getName() : "unknown";
        log.info("Giảng viên {} chấm điểm bài nộp {} với số điểm {}", lecturer, submissionId, score);
    }
}