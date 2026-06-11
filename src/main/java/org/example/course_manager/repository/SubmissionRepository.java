package org.example.course_manager.repository;

import org.example.course_manager.entity.Course;
import org.example.course_manager.entity.Submission;
import org.example.course_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudent(User student);
    List<Submission> findByCourseId(Long courseId);
    boolean existsByStudentAndCourse(User student, Course course);
}