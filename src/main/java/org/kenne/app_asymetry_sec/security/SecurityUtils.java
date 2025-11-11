package org.kenne.app_asymetry_sec.security;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.kenne.app_asymetry_sec.user.User;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityUtils {

    public static @NonNull String getUserIdFromSecurityContext() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }
}
