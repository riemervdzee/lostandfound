package dev.riemer.lostandfound.service;

import dev.riemer.lostandfound.dto.UserLoginDto;
import dev.riemer.lostandfound.model.Role;
import dev.riemer.lostandfound.model.User;
import dev.riemer.lostandfound.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        String username = "testUser";
        String password = "password123";
        Role role = Role.ROLE_USER;

        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");
        user.setRole(role);

        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = authenticationService.registerUser(username, password, role);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(role, result.getRole());

        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testAuthenticateSuccessful() {
        String username = "testUser";
        String password = "password123";

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);

        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        authenticationService.authenticate(loginDto);

        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testAuthenticateUserNotFound() {
        String username = "unknownUser";
        String password = "password123";

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(loginDto));

        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        verify(userRepository, times(1)).findByUsername(username);
    }
}
