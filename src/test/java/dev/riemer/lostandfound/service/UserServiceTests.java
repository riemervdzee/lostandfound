package dev.riemer.lostandfound.service;

import dev.riemer.lostandfound.model.Role;
import dev.riemer.lostandfound.model.User;
import dev.riemer.lostandfound.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByUsername_UserExists() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUsername_UserDoesNotExist() {
        String username = "nonExistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername(username);
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        String username = "testUser";
        String password = "testPassword";
        Role role = Role.ROLE_USER;
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        UserDetails userDetails = userService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testLoadUserByUsername_UserDoesNotExist() {
        String username = "nonExistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }
}
