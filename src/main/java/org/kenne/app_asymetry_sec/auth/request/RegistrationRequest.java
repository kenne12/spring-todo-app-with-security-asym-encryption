package org.kenne.app_asymetry_sec.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequest {

    @NotBlank(message = "VALIDATION.REGISTRATION.FIRST_NAME.NOT_BLANK")
    @Size(
            min = 2,
            max = 50,
            message = "VALIDATION.REGISTRATION.FIRST_NAME.SIZE"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$",
            message = "VALIDATION.REGISTRATION.FIRST_NAME.PATTERN"
    )
    @Schema(example = "Gervais")
    private String firstName;

    @NotBlank(message = "VALIDATION.REGISTRATION.LAST_NAME.NOT_BLANK")
    @Size(
            min = 2,
            max = 50,
            message = "VALIDATION.REGISTRATION.LAST_NAME.SIZE"
    )
    @Pattern(
            regexp = "^[\\p{L} '-]+$",
            message = "VALIDATION.REGISTRATION.LAST_NAME.PATTERN"
    )
    @Schema(example = "Kenne")
    private String lastName;

    @NotBlank(message = "VALIDATION.REGISTRATION.EMAIL.NOT_BLANK")
    @Email(message = "VALIDATION.REGISTRATION.EMAIL.FORMAT")
    // @NonDisposableEmail(message = "VALIDATION.REGISTRATION.EMAIL.DISPOSABLE")
    @Schema(example = "kennegervais@gmail.com")
    private String email;

    @NotBlank(message = "VALIDATION.REGISTRATION.PHONE_NUMBER.NOT_BLANK")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "VALIDATION.REGISTRATION.PHONE_NUMBER.PATTERN"
    )
    @Schema(example = "+1234567890") // Example french phone number
    private String phoneNumber;

    @NotBlank(message = "VALIDATION.REGISTRATION.PASSWORD.NOT_BLANK")
    @Size(
            min = 8,
            max = 30,
            message = "VALIDATION.REGISTRATION.PASSWORD.SIZE"
    )
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,30}$",
            message = "VALIDATION.REGISTRATION.PASSWORD.PATTERN"
    )
    @Schema(example = "StrongPassword123!")
    private String password;

    @NotBlank(message = "VALIDATION.REGISTRATION.CONFIRM_PASSWORD.NOT_BLANK")
    private String confirmPassword;

}
