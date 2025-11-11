package org.kenne.app_asymetry_sec.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kenne.app_asymetry_sec.exception.BusinessException;
import org.kenne.app_asymetry_sec.exception.ErrorCode;
import org.kenne.app_asymetry_sec.user.User;
import org.kenne.app_asymetry_sec.user.UserMapper;
import org.kenne.app_asymetry_sec.user.UserRepository;
import org.kenne.app_asymetry_sec.user.UserService;
import org.kenne.app_asymetry_sec.user.request.ChangePasswordRequest;
import org.kenne.app_asymetry_sec.user.request.ProfileUpdateRequest;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    @Override
    public void updateUserProfileInfo(
            @NonNull ProfileUpdateRequest request,
            @NonNull String userId
    ) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        userMapper.mergeUserInfo(user, request);

        userRepository.save(user);
    }

    @Override
    public void changePassword(
            @NonNull ChangePasswordRequest request,
            @NonNull String userId
    ) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "Passwords do not match");
        }

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        this.userRepository.save(user);
    }

    @Override
    public void deactivateAccount(@NonNull String userId) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        if (!user.isEnabled()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_DEACTIVATED);
        }

        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void reactivateAccount(@NonNull String userId) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        if (user.isEnabled()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_ACTIVATED);
        }

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void deleteAccount(@NonNull String userId) {
        throw new RuntimeException("Delete account functionality is not implemented yet.");

        // the logic is just to scheduled a profile deletion task

        // and the a scheduled job will pick the profile and delete everything related to the profile
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String userEmail) throws UsernameNotFoundException {
        return this.userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));
    }
}
