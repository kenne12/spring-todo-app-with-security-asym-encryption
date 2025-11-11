package org.kenne.app_asymetry_sec.handler;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kenne.app_asymetry_sec.exception.BusinessException;
import org.kenne.app_asymetry_sec.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.kenne.app_asymetry_sec.exception.ErrorCode.BAD_CREDENTIALS;
import static org.kenne.app_asymetry_sec.exception.ErrorCode.ERROR_USER_DISABLED;
import static org.kenne.app_asymetry_sec.exception.ErrorCode.INTERNAL_EXCEPTION;
import static org.kenne.app_asymetry_sec.exception.ErrorCode.USER_NAME_NOT_FOUND;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException ex) {
        var errorResponse = ErrorResponse
                .builder()
                .message(ex.getMessage())
                .code(ex.getErrorCode().getCode())
                .build();

        return ResponseEntity.status(
                ex.getErrorCode().getHttpStatus() != null ? ex.getErrorCode().getHttpStatus() : HttpStatus.BAD_REQUEST
        ).body(errorResponse);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledException(final DisabledException ex) {
        log.debug(ex.getMessage(), ex);
        var errorResponse = buildErrorResponse(ERROR_USER_DISABLED);

        return ResponseEntity.status(ERROR_USER_DISABLED.getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(final BadCredentialsException ex) {
        log.debug(ex.getMessage(), ex);
        var errorResponse = buildErrorResponse(BAD_CREDENTIALS);

        return ResponseEntity.status(BAD_CREDENTIALS.getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(final UsernameNotFoundException ex) {
        log.debug(ex.getMessage(), ex);
        var errorResponse = buildErrorResponse(USER_NAME_NOT_FOUND);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(final EntityNotFoundException ex) {
        log.debug(ex.getMessage(), ex);
        var errorResponse = ErrorResponse
                .builder()
                .message(ex.getMessage())
                .code("TO_BE_DEFINED")
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        log.debug(ex.getMessage(), ex);
        final var validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> ErrorResponse.ValidationError
                        .builder()
                        .field(fieldError.getField())
                        .code(fieldError.getCode())
                        .message(fieldError.getDefaultMessage())
                        .build())
                .toList();

        final var errorResponse = ErrorResponse
                .builder()
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthDeniedException(final AuthorizationDeniedException ex) {
        log.debug(ex.getMessage(), ex);
        var errorResponse = ErrorResponse.builder()
                .message("You do not have permission to perform this action.")
                .code("AUTHORIZATION_DENIED")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception ex) {
        log.error(ex.getMessage(), ex);
        var errorResponse = buildErrorResponse(INTERNAL_EXCEPTION);

        return ResponseEntity.status(INTERNAL_EXCEPTION.getHttpStatus())
                .body(errorResponse);
    }

    private @NonNull ErrorResponse buildErrorResponse(@NonNull ErrorCode errorCode) {
        return ErrorResponse
                .builder()
                .message(errorCode.getDefaultMessage())
                .code(errorCode.getCode())
                .build();
    }
}
