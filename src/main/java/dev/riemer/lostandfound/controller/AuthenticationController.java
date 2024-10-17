package dev.riemer.lostandfound.controller;

import dev.riemer.lostandfound.dto.UserLoginDto;
import dev.riemer.lostandfound.dto.UserLoginResponseDto;
import dev.riemer.lostandfound.model.User;
import dev.riemer.lostandfound.security.JwtUtil;
import dev.riemer.lostandfound.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller. Only used for logging in for now
 */
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtUtil jwtUtil;
    private final AuthenticationService authenticationService;

    /**
     * Constructor of the AuthenticationController.
     *
     * @param jwtUtil               injected JwtUtil
     * @param authenticationService injected AuthenticationService
     */
    public AuthenticationController(final JwtUtil jwtUtil, final AuthenticationService authenticationService) {
        this.jwtUtil = jwtUtil;
        this.authenticationService = authenticationService;
    }

    /**
     * Authentication endpoint, allows the user to log in with their credentials and returns a JWT token which can be
     * used for all other requests.
     *
     * @param loginUserDto the username+password to validate
     * @return Returns the JWT token if authentication is successful
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> authenticate(final @Valid @RequestBody UserLoginDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtUtil.generateToken(authenticatedUser);

        UserLoginResponseDto loginResponse = new UserLoginResponseDto();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtUtil.getExpirationTimeMs());

        return ResponseEntity.ok(loginResponse);
    }
}
