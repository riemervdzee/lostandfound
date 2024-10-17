package dev.riemer.lostandfound.controller;

import dev.riemer.lostandfound.dto.UserLoginDto;
import dev.riemer.lostandfound.dto.UserLoginResponseDto;
import dev.riemer.lostandfound.model.User;
import dev.riemer.lostandfound.security.JwtUtil;
import dev.riemer.lostandfound.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTests {

    private AuthenticationController authenticationController;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController(jwtUtil, authenticationService);
    }

    @Test
    public void testAuthenticate_Success() {
        // Prepare test data
        UserLoginDto loginUserDto = new UserLoginDto();
        loginUserDto.setUsername("testuser");
        loginUserDto.setPassword("password");

        User authenticatedUser = new User();
        authenticatedUser.setUsername("testuser");

        String token = "jwt-token";
        long expiresIn = 3600000L;

        // Mock behavior
        when(authenticationService.authenticate(loginUserDto)).thenReturn(authenticatedUser);
        when(jwtUtil.generateToken(authenticatedUser)).thenReturn(token);
        when(jwtUtil.getExpirationTimeMs()).thenReturn(expiresIn);

        // Call the method under test
        ResponseEntity<UserLoginResponseDto> response = authenticationController.authenticate(loginUserDto);

        // Verify
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        UserLoginResponseDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(token, responseBody.getToken());
        assertEquals(expiresIn, responseBody.getExpiresIn());

        // Verify interactions
        verify(authenticationService, times(1)).authenticate(loginUserDto);
        verify(jwtUtil, times(1)).generateToken(authenticatedUser);
        verify(jwtUtil, times(1)).getExpirationTimeMs();
    }

    @Test
    public void testAuthenticate_Failure() {
        // Prepare test data
        UserLoginDto loginUserDto = new UserLoginDto();
        loginUserDto.setUsername("testuser");
        loginUserDto.setPassword("wrongpassword");

        // Mock behavior
        when(authenticationService.authenticate(loginUserDto))
                .thenThrow(new RuntimeException("Authentication failed"));

        // Call the method under test and expect an exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationController.authenticate(loginUserDto);
        });

        assertEquals("Authentication failed", exception.getMessage());

        // Verify interactions
        verify(authenticationService, times(1)).authenticate(loginUserDto);
        verifyNoMoreInteractions(jwtUtil);
    }
}
