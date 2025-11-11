package org.kenne.app_asymetry_sec.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kenne.app_asymetry_sec.user.request.ChangePasswordRequest;
import org.kenne.app_asymetry_sec.user.request.ProfileUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Users API")
public class UserController {

    private final UserService userService;

    @PatchMapping("/me")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateProfile(
            @RequestBody @Valid ProfileUpdateRequest request,
            Authentication principal
    ) {
        userService.updateUserProfileInfo(request, getUserId(principal));
    }

    @PostMapping("/me/password")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Authentication principal
    ) {
        userService.changePassword(request, getUserId(principal));
    }

    @PatchMapping("/me/deactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deactivateAccount(Authentication principal) {
        userService.deactivateAccount(getUserId(principal));
    }

    @PatchMapping("/me/reactivate")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void reactivateAccount(Authentication principal) {
        userService.reactivateAccount(getUserId(principal));
    }

    @DeleteMapping("/me/delete")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteAccount(Authentication principal) {
        userService.deleteAccount(getUserId(principal));
    }

    private String getUserId(Authentication principal) {
        return ((User) principal.getPrincipal()).getId();
    }
}
