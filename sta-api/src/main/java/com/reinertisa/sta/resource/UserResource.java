package com.reinertisa.sta.resource;

import com.reinertisa.sta.domain.Response;
import com.reinertisa.sta.dto.User;
import com.reinertisa.sta.dtorequest.*;
import com.reinertisa.sta.enumaration.TokenType;
import com.reinertisa.sta.service.JwtService;
import com.reinertisa.sta.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

import static com.reinertisa.sta.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(path = {"/user"})
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Response> saveUser(@RequestBody @Valid UserRequest user, HttpServletRequest request) {
        userService.createUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword());
        return ResponseEntity.created(getUri())
                .body(getResponse(request, emptyMap(),
                        "Account created. Check your email to enable your account.", CREATED));
    }

    @GetMapping("/verify/account")
    public ResponseEntity<Response> verifyAccount(@RequestParam("key") String key, HttpServletRequest request) {
        userService.verifyAccountKey(key);
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account verified.", OK));
    }

    @GetMapping("/profile")
    public ResponseEntity<Response> profile(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        User user = userService.getUserByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "Profile retrieved.", OK));
    }

    @PatchMapping("/update")
    public ResponseEntity<Response> update(@AuthenticationPrincipal User userPrincipal,
                                           @RequestBody UserRequest userRequest, HttpServletRequest request) {
        User user = userService.updateUser(userPrincipal.getUserId(), userRequest.getFirstName(),
                userRequest.getLastName(), userRequest.getEmail(), userRequest.getPhone(), userRequest.getBio());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "User updated successfully", OK));
    }

    @PatchMapping("/updaterole")
    public ResponseEntity<Response> updateRole(@AuthenticationPrincipal User userPrincipal,
                                               @RequestBody RoleRequest roleRequest, HttpServletRequest request) {
        userService.updateRole(userPrincipal.getUserId(), roleRequest.getRole());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Role updated successfully", OK));
    }

    @PatchMapping("/toggleaccountexpired")
    public ResponseEntity<Response> toggleAccountExpired(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountExpired(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account updated successfully.", OK));
    }

    @PatchMapping("/toggleaccountlocked")
    public ResponseEntity<Response> toggleAccountLocked(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountLocked(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account updated successfully.", OK));
    }

    @PatchMapping("/toggleaccountenabled")
    public ResponseEntity<Response> toggleAccountEnabled(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleAccountEnabled(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account updated successfully.", OK));
    }

    @PatchMapping("/togglecredentialsexpired")
    public ResponseEntity<Response> toggleCredentialsExpired(@AuthenticationPrincipal User user, HttpServletRequest request) {
        userService.toggleCredentialsExpired(user.getUserId());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Account updated successfully.", OK));
    }

    @PatchMapping("/mfa/setup")
    public ResponseEntity<Response> setupMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        User user = userService.setUpMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "MFA set up successfully.", OK));
    }

    @PatchMapping("/mfa/cancel")
    public ResponseEntity<Response> cancelMfa(@AuthenticationPrincipal User userPrincipal, HttpServletRequest request) {
        User user = userService.cancelMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "MFA canceled successfully.", OK));
    }

    @PostMapping("/verify/qrcode")
    public ResponseEntity<Response> verifyQrCode(@RequestBody QrCodeRequest qrCodeRequest, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.verifyQrCode(qrCodeRequest.getUserId(), qrCodeRequest.getQrCode());
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "QR code verified.", OK));
    }

    // START - Reset password when user is not logged in
    @PostMapping("/resetpassword")
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid EmailRequest emailRequest, HttpServletRequest request) {
        userService.resetPassword(emailRequest.getEmail());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "We sent you an email to reset your password.", OK));
    }

    @GetMapping("/verify/password")
    public ResponseEntity<Response> verifyPassword(@RequestParam("key") String key, HttpServletRequest request) {
        User user = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "Enter new password", OK));
    }

    @PostMapping("/resetpassword/reset")
    public ResponseEntity<Response> doResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest, HttpServletRequest request) {
        userService.updatePassword(resetPasswordRequest.getUserId(), resetPasswordRequest.getNewPassword(), resetPasswordRequest.getConfirmNewPassword());
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Password reset successfully", OK));
    }
    // END - Reset password when user is not logged in

    private URI getUri() {
        return URI.create("");
    }
}
