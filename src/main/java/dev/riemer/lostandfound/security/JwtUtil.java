package dev.riemer.lostandfound.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for generating and parsing JWT Tokens.
 */
@Component
public final class JwtUtil {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time-ms}")
    private long jwtExpirationMs;

    /**
     * Verifies and returns the UserName stored in the JWT Token.
     *
     * @param token the token to parse
     * @return the username as String in the JWT token.
     */
    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param token          the token to parse
     * @param claimsResolver reference to function to retrieve the claim
     * @param <T>            the claim type, should be inferred automatically
     * @return the found claim
     */
    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a new JWT token given the UserDetails.
     *
     * @param userDetails the UserDetails which should be stored in the JWT token
     * @return the generated JWT token
     */
    public String generateToken(final UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a new JWT token given the UserDetails and extra claims.
     *
     * @param extraClaims the UserDetails which should be stored in the JWT token
     * @param userDetails A map of extra claims the user wishes to store
     * @return the generated JWT token
     */
    public String generateToken(final Map<String, Object> extraClaims, final UserDetails userDetails) {
        extraClaims.put("roles", userDetails.getAuthorities());
        return buildToken(extraClaims, userDetails);
    }

    /**
     * Getter for the current expiration time.
     *
     * @return the expiration time in milliseconds
     */
    public long getExpirationTimeMs() {
        return jwtExpirationMs;
    }

    /**
     * Checks whether the JWT token is valid, checked against the private key and the provided user-details.
     *
     * @param token       the JWT token to check
     * @param userDetails the userdetails, also used for checking
     * @return true if the JWT is valid
     */
    public boolean isTokenValid(final String token, final UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Uses Jwts to build up the JWT token.
     *
     * @param extraClaims the extra claims to store
     * @param userDetails the user-details to store
     * @return the JWT in String format
     */
    private String buildToken(final Map<String, Object> extraClaims, final UserDetails userDetails) {
        var now = System.currentTimeMillis();

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + jwtExpirationMs))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Simple checker whether the JWT token is expired or not.
     *
     * @param token the JWT token to check
     * @return true if this token is expired
     */
    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the Expiration date of the JWT token.
     *
     * @param token the JWT token to check
     * @return the extracted Expiration
     */
    private Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Verifies and extracts all claims of the JWT token.
     *
     * @param token the JWT token to check
     * @return The Claims stored inside the JWT
     */
    private Claims extractAllClaims(final String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Utility function to get the SecretKey from the properties in the right format.
     *
     * @return The SecretKey to be used in Jwts
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
