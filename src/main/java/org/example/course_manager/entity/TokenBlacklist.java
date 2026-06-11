package org.example.course_manager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="token_blacklist")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TokenBlacklist {
    @Id
    private String token;
    private Instant expiryDate;
}
