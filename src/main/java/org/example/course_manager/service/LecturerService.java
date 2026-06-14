package org.example.course_manager.service;

import lombok.RequiredArgsConstructor;
import org.example.course_manager.entity.LectureMaterial;
import org.example.course_manager.entity.Course;
import org.example.course_manager.entity.User;
import org.example.course_manager.repository.LectureMaterialRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LecturerService {
    private final LectureMaterialRepository materialRepository;
    private final CourseService courseService;
    private final UserService userService;

    public LectureMaterial uploadMaterial(Long courseId, String fileName, String fileUrl, String lecturerUsername) {
        Course course = courseService.findById(courseId);
        User lecturer = userService.findByUsername(lecturerUsername);

        if (course.getLecturerId() == null || !course.getLecturerId().equals(lecturer.getId())) {
            throw new RuntimeException("Bạn không phải giảng viên của khóa học này");
        }

        LectureMaterial material = new LectureMaterial();
        material.setCourse(course);
        material.setFileName(fileName);
        material.setFileUrl(fileUrl);
        return materialRepository.save(material);
    }
}