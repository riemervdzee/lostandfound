package dev.riemer.lostandfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Input DTO for logging in a user.
 */
@Data
public class UserLoginDto {

    @NotBlank(message = "Username must not be blank.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @NotBlank(message = "Password must not be blank.")
    @Size(min = 6, message = "Password must be at least 6 characters long.")
    private String password;
}
