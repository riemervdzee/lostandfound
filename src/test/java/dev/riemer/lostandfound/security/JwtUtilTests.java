package dev.riemer.lostandfound.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtUtilTests {
    @Autowired
    private JwtUtil jwtUtil;

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        // Create a UserDetails instance
        userDetails = new User(
                "testuser",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ADMIN"))
        );
    }

    @Test
    public void testGenerateToken() {
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
    }

    @Test
    public void testExtractUsername() {
        String token = jwtUtil.generateToken(userDetails);
        String username = jwtUtil.extractUsername(token);
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    public void testIsTokenValid() {
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.isTokenValid(token, userDetails));
    }

    @Test
    public void testIsTokenExpired() throws Exception {
        // Create a token with past expiration date
        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .expiration(new Date(System.currentTimeMillis() - 1000)) // Expired 1 second ago
                .signWith(getSignInKey())
                .compact();

        assertFalse(jwtUtil.isTokenValid(token, userDetails));
    }

    @Test
    public void testGenerateTokenWithExtraClaims() {
        HashMap<String, Object> collection = new HashMap<>();
        collection.put("claimKey", "claimValue");
        String token = jwtUtil.generateToken(collection, userDetails);
        assertNotNull(token);

        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("claimValue", claims.get("claimKey"));
    }

    @Test
    public void testExtractExpiration() {
        String token = jwtUtil.generateToken(userDetails);
        Date expiration = jwtUtil.extractClaim(token, Claims::getExpiration);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    public void testExtractAllClaims() {
        String token = jwtUtil.generateToken(userDetails);
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(userDetails.getUsername(), claims.getSubject());
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
