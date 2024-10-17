package dev.riemer.lostandfound.dto;

import lombok.Data;

/**
 * Data Transfer Object for user information.
 */
@Data
public class UserInfoDto {
    private String username;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String telephone;
}
