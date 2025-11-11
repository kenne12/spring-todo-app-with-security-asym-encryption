package org.kenne.app_asymetry_sec.auth.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kenne.app_asymetry_sec.auth.AuthenticationService;
import org.kenne.app_asymetry_sec.auth.request.AuthenticationRequest;
import org.kenne.app_asymetry_sec.auth.request.RefreshRequest;
import org.kenne.app_asymetry_sec.auth.request.RegistrationRequest;
import org.kenne.app_asymetry_sec.auth.response.AuthenticationResponse;
import org.kenne.app_asymetry_sec.exception.BusinessException;
import org.kenne.app_asymetry_sec.exception.ErrorCode;
import org.kenne.app_asymetry_sec.role.Role;
import org.kenne.app_asymetry_sec.role.RoleRepository;
import org.kenne.app_asymetry_sec.security.JwtService;
import org.kenne.app_asymetry_sec.user.User;
import org.kenne.app_asymetry_sec.user.UserMapper;
import org.kenne.app_asymetry_sec.user.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    @Override
    public @NonNull AuthenticationResponse login(@NonNull AuthenticationRequest request) {
        log.info("Processing login for user: {}", request.getEmail());

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        final User user = (User) authentication.getPrincipal();

        final String token = jwtService.generateAccessToken(user.getUsername());
        final String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return AuthenticationResponse
                .builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public void register(@NonNull RegistrationRequest request) {
        checkUserEmail(request.getEmail());
        checkUserPhoneNumber(request.getPhoneNumber());
        checkPasswords(request.getPassword(), request.getConfirmPassword());

        final Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException("Role user not found"));

        final List<Role> roles = List.of(role);

        final User user = this.userMapper.toUser(request);
        user.setRoles(roles);

        log.debug("Registering user: {}", user);

        userRepository.save(user);
    }

    private void checkUserEmail(@NonNull String email) {
        final boolean emailExists = userRepository.existsByEmailIgnoreCase(email);

        if (emailExists) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private void checkUserPhoneNumber(String phoneNumber) {
        final boolean phoneNumberExists = userRepository.existsByPhoneNumber(phoneNumber);

        if (phoneNumberExists) {
            throw new BusinessException(ErrorCode.PHONE_NUMBER_ALREADY_EXISTS);
        }
    }

    private void checkPasswords(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    @Override
    public @NonNull AuthenticationResponse refreshToken(@NonNull RefreshRequest request) {
        final var newToken = jwtService.refreshAccessToken(request.getRefreshToken());

        return AuthenticationResponse
                .builder()
                .accessToken(newToken)
                .refreshToken(request.getRefreshToken())
                .tokenType("Bearer")
                .build();
    }
}
