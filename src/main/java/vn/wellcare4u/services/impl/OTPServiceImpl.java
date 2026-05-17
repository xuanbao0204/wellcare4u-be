package vn.wellcare4u.services.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.entities.OtpToken;
import vn.wellcare4u.enums.EOTPType;
import vn.wellcare4u.exception.AppException;
import vn.wellcare4u.models.request.OtpRequest;
import vn.wellcare4u.repositories.OtpTokenRepository;
import vn.wellcare4u.services.EmailService;
import vn.wellcare4u.services.OTPService;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private final OtpTokenRepository otpTokenRepo;
    private final EmailService emailService;

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int OTP_COOLDOWN_SECONDS = 60;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void sendOtp(OtpRequest req) {

        otpTokenRepo.deleteAllByEmail(req.getEmail());

        String code = generateOtpCode();

        OtpToken otp = OtpToken.builder()
                .email(req.getEmail())
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .type(req.getType())
                .createdAt(LocalDateTime.now())
                .build();

        otpTokenRepo.save(otp);

        emailService.sendOtpEmail(req.getEmail(), req.getEmail().split("@")[0], code);
    }

    @Override
    public void verifyOtp(OtpRequest req) {

        OtpToken otp = otpTokenRepo.findTopByEmailOrderByCreatedAtDesc(req.getEmail())
                .orElseThrow(() -> new AppException(
                        "Không tìm thấy mã OTP",
                        "OTP_NOT_FOUND",
                        HttpStatus.NOT_FOUND
                ));

        if (otp.isUsed()) {
            throw new AppException(
                    "Mã OTP đã được sử dụng",
                    "OTP_USED",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(
                    "Mã OTP đã hết hạn",
                    "OTP_EXPIRED",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (!otp.getCode().equals(req.getCode())) {
            throw new AppException(
                    "Mã OTP không chính xác",
                    "OTP_INVALID",
                    HttpStatus.BAD_REQUEST
            );
        }
        
        if (!otp.getType().equals(req.getType())) {
        	throw new AppException(
                    "Mã OTP không hợp lệ",
                    "OTP_INVALID",
                    HttpStatus.BAD_REQUEST
            );
        }

        otp.setUsed(true);

        otpTokenRepo.save(otp);
    }

    @Override
    public void resendOtp(OtpRequest req) {

        otpTokenRepo.findTopByEmailOrderByCreatedAtDesc(req.getEmail())
                .ifPresent(prev -> {

                    if (prev.getCreatedAt()
                            .plusSeconds(OTP_COOLDOWN_SECONDS)
                            .isAfter(LocalDateTime.now())) {

                        throw new AppException(
                                "Vui lòng chờ 60 giây trước khi gửi lại OTP",
                                "OTP_COOLDOWN",
                                HttpStatus.TOO_MANY_REQUESTS
                        );
                    }
                });

        sendOtp(new OtpRequest(req.getEmail(), req.getType()));
    }

    private String generateOtpCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}