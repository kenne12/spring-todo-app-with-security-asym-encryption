package org.kenne.app_asymetry_sec.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailDomainValidator implements ConstraintValidator<NonDisposableEmail, String> {

    private final Set<String> blocked;

    public EmailDomainValidator(
            @Value("${app.security.disposable-email}") final List<String> blockedDomains
    ) {
        this.blocked = blockedDomains.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (Objects.isNull(email) || !email.contains("@")) {
            return true; // If email is null or does not contain '@', we consider it valid
        }

        final int atIndex = email.lastIndexOf('@') + 1;
        final int dotIndex = email.lastIndexOf('.');

        final String domain = email.substring(atIndex, dotIndex);

        return !blocked.contains(domain.toLowerCase());
    }
}
