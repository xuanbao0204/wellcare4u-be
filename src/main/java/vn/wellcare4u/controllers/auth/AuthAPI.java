package vn.wellcare4u.controllers.auth;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.request.LoginRequest;
import vn.wellcare4u.models.request.RegisterRequest;
import vn.wellcare4u.services.AuthService;
import vn.wellcare4u.services.UserService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthAPI {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletResponse response) {

        var result = authService.login(request);

        response.addHeader(HttpHeaders.SET_COOKIE,
                buildAccessCookie(result.getAccessToken()).toString());

        response.addHeader(HttpHeaders.SET_COOKIE,
                buildRefreshCookie(result.getRefreshToken()).toString());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Đăng nhập thành công")
                        .data(result.getUser())
                        .build()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        authService.register(request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Đăng ký thành công")
                        .build()
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(401).body(
                    ApiResponse.builder()
                            .status(401)
                            .message("Missing refresh token")
                            .build()
            );
        }

        var result = authService.refreshToken(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE,
                buildAccessCookie(result.getAccessToken()).toString());

        response.addHeader(HttpHeaders.SET_COOKIE,
                buildRefreshCookie(result.getRefreshToken()).toString());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Token refreshed")
                        .build()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();

        return ResponseEntity.ok(userService.getUserInfoByEmail(email));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication,
                                    HttpServletResponse response) {

        if (authentication != null && authentication.isAuthenticated()) {
            authService.logout(authentication.getName());
        }

        response.addHeader(HttpHeaders.SET_COOKIE,
                clearCookie("accessToken").toString());

        response.addHeader(HttpHeaders.SET_COOKIE,
                clearCookie("refreshToken").toString());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Đăng xuất thành công")
                        .build()
        );
    }

    private ResponseCookie buildAccessCookie(String token) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(true) //ddooir veef true khi production https
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofMinutes(30))
                .build();
    }

    private ResponseCookie buildRefreshCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(7))
                .build();
    }

    private ResponseCookie clearCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
    }
}