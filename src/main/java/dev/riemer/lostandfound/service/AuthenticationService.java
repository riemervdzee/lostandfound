package dev.riemer.lostandfound.service;

import dev.riemer.lostandfound.dto.UserLoginDto;
import dev.riemer.lostandfound.model.Role;
import dev.riemer.lostandfound.model.User;
import dev.riemer.lostandfound.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for User Authentication.
 */
@Service
public class AuthenticationService {
    private final UserRepository repository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor of AuthenticationService.
     *
     * @param userRepository        injected UserRepository
     * @param authenticationManager injected AuthenticationManager
     * @param passwordEncoder       injected PasswordEncoder
     */
    public AuthenticationService(
            final UserRepository userRepository,
            final AuthenticationManager authenticationManager,
            final PasswordEncoder passwordEncoder
    ) {
        this.repository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user, provides password hashing and saving in the database.
     *
     * @param username username of new user, should be unique
     * @param password password of new user
     * @param role     the role of the new user
     * @return the newly created User Entity
     */
    public User registerUser(final String username, final String password, final Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        return repository.save(user);
    }

    /**
     * Authenticates a User Login request, returning the User Entity if successful.
     *
     * @param input the UserLogin request
     * @return the logged-in User entity
     */
    public User authenticate(final UserLoginDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getUsername(), input.getPassword())
        );

        return repository.findByUsername(input.getUsername()).orElseThrow();
    }
}
