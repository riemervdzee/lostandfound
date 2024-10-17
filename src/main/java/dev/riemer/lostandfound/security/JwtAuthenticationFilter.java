package dev.riemer.lostandfound.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * RequestFilter which checks, validates and sets the Identity when a JWT token is given in the Authorization Header.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * Constructs the JwtAuthenticationFilter.
     *
     * @param jwtUtil                  injected JwtUtil
     * @param userDetailsService       injected userDetailsService
     * @param handlerExceptionResolver the global HandlerExceptionResolver
     */
    public JwtAuthenticationFilter(
            final JwtUtil jwtUtil,
            final UserDetailsService userDetailsService,
            final HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /**
     * Check, validates and sets the Identity when a JWT token is given in the Authorization Header.
     *
     * @param request     the request to check and validate
     * @param response    the response so far
     * @param filterChain the rest of the FilterChain
     * @throws ServletException thrown when something is wrong in the rest of the filter-chain
     * @throws IOException      thrown when something is wrong in the rest of the filter-chain
     */
    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Remove "Bearer " part and get the username
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtUtil.extractUsername(jwt);

            // Set SecurityContext Identity if a User is found
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userEmail != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            // Re-use our global Exception handler to use the standardized error handling
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
