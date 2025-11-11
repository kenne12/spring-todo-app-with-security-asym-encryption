package org.kenne.app_asymetry_sec.user;

import org.kenne.app_asymetry_sec.user.request.ChangePasswordRequest;
import org.kenne.app_asymetry_sec.user.request.ProfileUpdateRequest;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void updateUserProfileInfo(ProfileUpdateRequest request, @NonNull String userId);

    void changePassword(ChangePasswordRequest request, @NonNull String userId);

    void deactivateAccount(@NonNull String userId);

    void reactivateAccount(@NonNull String userId);

    void deleteAccount(@NonNull String userId);
}
