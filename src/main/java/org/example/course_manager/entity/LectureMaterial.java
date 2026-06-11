package org.example.course_manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_materials")
@Getter
@Setter
@NoArgsConstructor
public class LectureMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String fileName;
    private String fileUrl;
    private LocalDateTime uploadedAt = LocalDateTime.now();
}