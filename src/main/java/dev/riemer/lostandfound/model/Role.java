package dev.riemer.lostandfound.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

/**
 * Defines the Roles used in our application. A User is either a normal user, or an admin.
 */
@Getter
public enum Role implements GrantedAuthority {
    /**
     * Grants the normal User rights to a User.
     */
    ROLE_USER("USER"),
    /**
     * Grants the Admin rights to a User. This also includes normal User rights.
     */
    ROLE_ADMIN("ADMIN");

    private final String value;

    /**
     * Constructs a Role obj with string representation.
     *
     * @param value the string value without the ROLE_ part. used for serialization.
     */
    Role(final String value) {
        this.value = value;
    }

    /**
     * Implemented for GrantedAuthority, allows this Role enum to be used within Spring Security.
     *
     * @return a String representation of the Role without the ROLE_ prefix.
     */
    @Override
    public String getAuthority() {
        return name();
    }
}
