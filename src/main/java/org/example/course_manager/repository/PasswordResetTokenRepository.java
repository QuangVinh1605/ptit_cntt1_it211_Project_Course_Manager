// org.example.course_manager.repository.PasswordResetTokenRepository
package org.example.course_manager.repository;

import org.example.course_manager.entity.PasswordResetToken;
import org.example.course_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);
}