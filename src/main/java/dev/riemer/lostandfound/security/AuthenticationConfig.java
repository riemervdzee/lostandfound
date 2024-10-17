package dev.riemer.lostandfound.security;

import dev.riemer.lostandfound.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 */
@Configuration
public class AuthenticationConfig {
    private final UserRepository userRepository;

    /**
     * The Constructor of AuthenticationConfig.
     *
     * @param userRepository the injected UserRepository
     */
    public AuthenticationConfig(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Helps get the current UserDetails object of the current User by JWT token.
     *
     * @return a lambda for which uses our UserRepository for as UserDetailsService
     */
    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * The used PasswordEncoder our project.
     *
     * @return A PasswordEncoder
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Use the generic AuthenticationManager.
     *
     * @param config the injected Spring Auth config
     * @return default AuthenticationManager
     * @throws Exception thrown when misconfigured
     */
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Returns our overloaded DaoAuthenticationProvider with our UserDetails config.
     *
     * @return the overloaded AuthenticationProvider
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
