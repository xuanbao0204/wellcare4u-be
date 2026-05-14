package vn.wellcare4u.services;

import vn.wellcare4u.models.request.LoginRequest;
import vn.wellcare4u.models.request.RegisterRequest;
import vn.wellcare4u.models.response.LoginResponse;

public interface AuthService {

	LoginResponse login(LoginRequest req);

	void register(RegisterRequest req);

	LoginResponse refreshToken(String refreshToken);

	void logout(String email);

	void resendOtp(String email);

	void verifyOtp(String email, String code);

}
