package org.example.course_manager.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.course_manager.constant.Role;


@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @JsonProperty(access= JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Column(unique=true,  nullable=false)
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean active=true;
}
