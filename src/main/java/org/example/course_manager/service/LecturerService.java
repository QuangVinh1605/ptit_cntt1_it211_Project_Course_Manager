package org.example.course_manager.service;

import lombok.RequiredArgsConstructor;
import org.example.course_manager.entity.LectureMaterial;
import org.example.course_manager.entity.Course;
import org.example.course_manager.repository.LectureMaterialRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LecturerService {
    private final LectureMaterialRepository materialRepository;
    private final CourseService courseService;

    public LectureMaterial uploadMaterial(Long courseId, String fileName, String fileUrl) {
        Course course = courseService.findById(courseId);
        LectureMaterial material = new LectureMaterial();
        material.setCourse(course);
        material.setFileName(fileName);
        material.setFileUrl(fileUrl);
        return materialRepository.save(material);
    }
}