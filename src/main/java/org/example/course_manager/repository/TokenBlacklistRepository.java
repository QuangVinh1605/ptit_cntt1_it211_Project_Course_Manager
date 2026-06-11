package org.example.course_manager.repository;

import org.example.course_manager.entity.TokenBlacklist;
import org.springframework.data.repository.CrudRepository;

public interface TokenBlacklistRepository extends CrudRepository<TokenBlacklist, String> {
    boolean existsById(String token);
}