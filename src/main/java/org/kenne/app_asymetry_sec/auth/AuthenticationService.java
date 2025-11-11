package org.kenne.app_asymetry_sec.auth;

import org.kenne.app_asymetry_sec.auth.request.AuthenticationRequest;
import org.kenne.app_asymetry_sec.auth.request.RefreshRequest;
import org.kenne.app_asymetry_sec.auth.request.RegistrationRequest;
import org.kenne.app_asymetry_sec.auth.response.AuthenticationResponse;
import org.springframework.lang.NonNull;

public interface AuthenticationService {

    @NonNull
    AuthenticationResponse login(@NonNull AuthenticationRequest request);

    void register(@NonNull RegistrationRequest request);

    @NonNull
    AuthenticationResponse refreshToken(@NonNull RefreshRequest request);
}
