package vn.wellcare4u.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.wellcare4u.models.ApiResponse;
import vn.wellcare4u.models.request.OtpRequest;
import vn.wellcare4u.services.OTPService;

@RestController
@RequestMapping("/api/v1/otp")
public class OtpAPI {

	@Autowired
	private OTPService otpServ;
	
	@PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpRequest request) {
 
        otpServ.sendOtp(request);
 
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Mã OTP mới đã được gửi đến email của bạn.")
                        .build()
        );
    }
	
	@PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest request) {
 
        otpServ.verifyOtp(request);
 
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Xác thực thành công!")
                        .build()
        );
    }
 
    @PostMapping("/resend")
    public ResponseEntity<?> resendOtp(@RequestBody OtpRequest request) {
 
        otpServ.resendOtp(request);
 
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Mã OTP mới đã được gửi đến email của bạn.")
                        .build()
        );
    }
	
}
