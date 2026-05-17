package vn.wellcare4u.services;

import vn.wellcare4u.models.request.OtpRequest;

public interface OTPService {

	void resendOtp(OtpRequest req);

	void verifyOtp(OtpRequest req);

	void sendOtp(OtpRequest req);

}
