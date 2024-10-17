package dev.riemer.lostandfound.service;

import dev.riemer.lostandfound.model.User;
import dev.riemer.lostandfound.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for getting Users.
 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository repository;

    /**
     * UserService Constructor.
     *
     * @param repository Injected UserRepository
     */
    UserService(final UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Gets a User Entity by its username.
     *
     * @param username the username to try to find
     * @return an optional User Entity
     */
    public Optional<User> findByUsername(final String username) {
        return repository.findByUsername(username);
    }

    /**
     * Get a UserDetails object to be used in Spring Security for the given username.
     *
     * @param username the username identifying the user whose data is required.
     * @return UserDetails object to be used in Spring
     * @throws UsernameNotFoundException thrown when the username could not be found
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findByUsername(username);

        return user
                .map(u -> new org.springframework.security.core.userdetails.User(
                        u.getUsername(), u.getPassword(), u.getAuthorities())
                )
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
