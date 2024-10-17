package dev.riemer.lostandfound.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

/**
 * Standardize the possible error states returned by the API, making sure no details are leaked.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Overrides the default Exception.class and provide our info, which can be returned via the API.
     *
     * @param exception the exception to process
     * @return a ProblemDetail used by Spring for the Error Response
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(final Exception exception) {
        ProblemDetail errorDetail;

        errorDetail = switch (exception) {

            case BadCredentialsException e -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
                pd.setProperty("description", "The username or password is incorrect");
                yield pd;
            }
            case AccountStatusException e -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
                pd.setProperty("description", "The account is locked");
                yield pd;
            }
            case AccessDeniedException e -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
                pd.setProperty("description", "You are not authorized to access this resource");
                yield pd;
            }
            case SignatureException e -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
                pd.setProperty("description", "The JWT signature is invalid");
                yield pd;
            }
            case ExpiredJwtException e -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, e.getMessage());
                pd.setProperty("description", "The JWT token has expired");
                yield pd;
            }
            case NoSuchElementException e -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
                pd.setProperty("description", "The resource could not be found");
                yield pd;
            }
            case MethodArgumentNotValidException e -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());

                e.getBindingResult().getFieldErrors().forEach(error ->
                        pd.setProperty(error.getField(), error.getDefaultMessage())
                );
                yield pd;
            }
            default -> {
                ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()
                );
                pd.setProperty("description", "Unknown internal server error.");
                // Only output stacktrace when we got 500 errors. In production we should use something like Sentry
                exception.printStackTrace();
                yield pd;
            }
        };

        return errorDetail;
    }
}
