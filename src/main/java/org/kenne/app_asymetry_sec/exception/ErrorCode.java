package org.kenne.app_asymetry_sec.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND("USER_NOT_FOUND", "User not found with id '%s'", HttpStatus.NOT_FOUND),

    CHANGE_PASSWORD_MISMATCH("CHANGE_PASSWORD_MISMATCH", "Current password and new password are the same", HttpStatus.BAD_REQUEST),

    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "Current password is invalid", HttpStatus.BAD_REQUEST),

    USER_ALREADY_DEACTIVATED("USER_ALREADY_DEACTIVATED", "User already deactivated", HttpStatus.BAD_REQUEST),

    USER_ALREADY_ACTIVATED("USER_ALREADY_ACTIVATED", "User already activated", HttpStatus.BAD_REQUEST),

    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists", HttpStatus.BAD_REQUEST),

    PHONE_NUMBER_ALREADY_EXISTS("PHONE_NUMBER_ALREADY_EXISTS", "Phone number already exists", HttpStatus.BAD_REQUEST),

    PASSWORD_MISMATCH("PASSWORD_MISMATCH", "Passwords do not match", HttpStatus.BAD_REQUEST),

    ERROR_USER_DISABLED("ERROR_USER_DISABLED", "User is disabled", HttpStatus.UNAUTHORIZED),

    BAD_CREDENTIALS("BAD_CREDENTIALS", "Username and / or password is incorrect", HttpStatus.UNAUTHORIZED),

    USER_NAME_NOT_FOUND("USER_NAME_NOT_FOUND", "Username not found", HttpStatus.UNAUTHORIZED),

    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    CATEGORY_ALREADY_EXISTS_FOR_USER("CATEGORY_ALREADY_EXISTS_FOR_USER", "Category already exists for this user", CONFLICT),
    ;

    private final String code;

    private final String defaultMessage;

    private final HttpStatus httpStatus;

    ErrorCode(final String code, final String defaultMessage, final HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}
