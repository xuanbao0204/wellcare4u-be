package vn.wellcare4u.services.impl;


import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.entities.Account;
import vn.wellcare4u.entities.RefreshToken;
import vn.wellcare4u.entities.User;
import vn.wellcare4u.enums.EAccountStatus;
import vn.wellcare4u.enums.ERole;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.factory.UserFactory;
import vn.wellcare4u.models.request.LoginRequest;
import vn.wellcare4u.models.request.RegisterRequest;
import vn.wellcare4u.models.response.LoginResponse;
import vn.wellcare4u.repositories.AccountRepository;
import vn.wellcare4u.services.AuthService;
import vn.wellcare4u.services.RefreshTokenService;
import vn.wellcare4u.services.UserService;
import vn.wellcare4u.utils.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accRepo;
    private final UserService userServ;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserFactory userFactory;
    private final RefreshTokenService refreshTokenService;
    
    @Override
    public void register(RegisterRequest req) {

        if (accRepo.existsByEmail(req.getEmail())) {
            throw new AppException(
                    "Tài khoản với email đã tồn tại",
                    "EMAIL_USED",
                    HttpStatus.BAD_REQUEST
            );
        }

        Account acc = new Account();
        acc.setEmail(req.getEmail());
        acc.setPassword(passwordEncoder.encode(req.getPassword()));
        acc.setRole(ERole.valueOf(req.getRole()));
        acc.setStatus(EAccountStatus.INACTIVE);
        acc.setCreatedAt(LocalDateTime.now());

        User user = userFactory.createUser(ERole.valueOf(req.getRole()));
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setAvatar("https://res.cloudinary.com/dlueiywku/image/upload/v1772988330/user_jzkljv.png");
        user.setAccount(acc);
        acc.setUser(user);

        accRepo.save(acc);
    }

    @Override
    public LoginResponse login(LoginRequest req) {

        Account acc = accRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new AppException(
                        "Tài khoản không tồn tại",
                        "ACCOUNT_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));

        if (!passwordEncoder.matches(req.getPassword(), acc.getPassword())) {
            throw new AppException(
                    "Tài khoản hoặc mật khẩu không chính xác",
                    "INVALID_PASSWORD",
                    HttpStatus.UNAUTHORIZED
            );
        }

        acc.setLastLoginAt(LocalDateTime.now());
        accRepo.save(acc);

        String accessToken = jwtUtil.generateAccessToken(acc);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(acc);
        
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .accStatus(acc.getStatus().name())
                .user(userServ.getUserInfo(acc.getId()))
                .build();
    }

    @Override
    public LoginResponse refreshToken(String refreshTokenStr) {

        RefreshToken oldToken = refreshTokenService.verify(refreshTokenStr);

        Account acc = oldToken.getAccount();

        refreshTokenService.deleteByAccount(acc);

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(acc);

        String newAccessToken = jwtUtil.generateAccessToken(acc);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .accStatus(acc.getStatus().name())
                .user(userServ.getUserInfo(acc.getId()))
                .build();
    }


    @Override
    public void logout(String email) {

        Account acc = accRepo.findByEmail(email)
                .orElseThrow(() -> new AppException(
                        "Tài khoản không tồn tại",
                        "ACCOUNT_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));

        refreshTokenService.deleteByAccount(acc);
    }
}