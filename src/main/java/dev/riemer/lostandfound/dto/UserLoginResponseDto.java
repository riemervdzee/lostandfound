package dev.riemer.lostandfound.dto;

import lombok.Data;

/**
 * Represents a JWT token after a successful login.
 */
@Data
public class UserLoginResponseDto {
    private String token;
    private long expiresIn;
}
