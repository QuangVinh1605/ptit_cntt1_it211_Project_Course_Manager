package org.example.course_manager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="courses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;
    private String name;
    private String description;
    @Column(name="lecturer_id")
    private Long lecturerId;
    private LocalDateTime createdAt=LocalDateTime.now();

}
