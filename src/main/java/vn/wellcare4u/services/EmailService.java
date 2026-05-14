package vn.wellcare4u.services;

public interface EmailService {

	void sendOtpEmail(String toEmail, String firstName, String otp);

}
