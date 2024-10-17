package dev.riemer.lostandfound.repository;

import dev.riemer.lostandfound.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository for getting User Entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Retrieves a User entity by username.
     *
     * @param username the Username to search by
     * @return returns the User if found
     */
    Optional<User> findByUsername(String username);
}
