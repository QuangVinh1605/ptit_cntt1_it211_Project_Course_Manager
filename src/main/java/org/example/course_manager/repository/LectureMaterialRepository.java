package org.example.course_manager.repository;

import org.example.course_manager.entity.LectureMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LectureMaterialRepository extends JpaRepository<LectureMaterial, Long> {
    List<LectureMaterial> findByCourseId(Long courseId);
}