package com.reinertisa.sta.resource;

import com.reinertisa.sta.domain.Response;
import com.reinertisa.sta.dtorequest.UserRequest;
import com.reinertisa.sta.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final AuthenticationManager authenticationManager;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest user) {
        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(
                user.getEmail(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(unauthenticated);
        return ResponseEntity.ok().body(Map.of("user", authenticate));
    }

    private URI getUri() {
        return URI.create("");
    }
}
