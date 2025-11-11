package org.kenne.app_asymetry_sec.exception;

import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    private final Object[] args;

    public BusinessException(
            final @NonNull ErrorCode errorCode,
            @Nullable final Object... args
    ) {
        super(getFormattedMessage(errorCode, args));

        this.errorCode = errorCode;
        this.args = args;
    }

    private static String getFormattedMessage(@NonNull ErrorCode errorCode, Object[] args) {

        if (Objects.nonNull(args) && args.length > 0) {
            return String.format(errorCode.getDefaultMessage(), args);
        }

        return errorCode.getDefaultMessage();
    }
}
