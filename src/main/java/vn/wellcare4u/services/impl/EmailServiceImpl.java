package vn.wellcare4u.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.wellcare4u.services.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendOtpEmail(String toEmail, String firstName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("WellCare4U – Mã xác thực tài khoản của bạn");
            helper.setText(buildHtml(firstName, otp), true);

            mailSender.send(message);
            log.info("OTP email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildHtml(String firstName, String otp) {
        StringBuilder digitBoxes = new StringBuilder();
        for (char c : otp.toCharArray()) {
            digitBoxes.append(
                "<td style=\"padding: 0 4px;\">" +
                "<div style=\"width:48px;height:56px;background:#f0f7ff;border:2px solid #3b82f6;" +
                "border-radius:10px;display:flex;align-items:center;justify-content:center;" +
                "font-size:28px;font-weight:700;color:#1e40af;text-align:center;line-height:56px;\">" +
                c + "</div></td>"
            );
        }

        return """
            <!DOCTYPE html>
            <html lang="vi">
            <head><meta charset="UTF-8"></head>
            <body style="margin:0;padding:0;background:#f1f5f9;font-family:'Segoe UI',Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f1f5f9;padding:40px 0;">
                <tr><td align="center">
                  <table width="520" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:20px;overflow:hidden;
                                box-shadow:0 8px 32px rgba(59,130,246,0.12);">

                    <!-- Header -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#2563eb,#0ea5e9);
                                 padding:36px 40px;text-align:center;">
                        <p style="margin:0;font-size:26px;font-weight:800;color:#fff;
                                  letter-spacing:-0.5px;">💊 WellCare4U</p>
                        <p style="margin:8px 0 0;font-size:14px;color:rgba(255,255,255,0.85);">
                          Nền tảng chăm sóc sức khỏe toàn diện
                        </p>
                      </td>
                    </tr>

                    <!-- Body -->
                    <tr>
                      <td style="padding:40px 40px 32px;">
                        <p style="margin:0 0 8px;font-size:20px;font-weight:700;color:#0f172a;">
                          Xin chào, %s! 👋
                        </p>
                        <p style="margin:0 0 28px;font-size:15px;color:#475569;line-height:1.6;">
                          Cảm ơn bạn đã đăng ký tài khoản tại <strong>WellCare4U</strong>.<br>
                          Hãy dùng mã OTP bên dưới để xác thực và kích hoạt tài khoản của bạn.
                        </p>

                        <!-- OTP boxes -->
                        <table cellpadding="0" cellspacing="0" style="margin:0 auto 28px;">
                          <tr>%s</tr>
                        </table>

                        <!-- Timer note -->
                        <div style="background:#fef9c3;border:1px solid #fde047;border-radius:10px;
                                    padding:12px 16px;text-align:center;margin-bottom:24px;">
                          <p style="margin:0;font-size:13px;color:#713f12;">
                            ⏱ Mã OTP có hiệu lực trong <strong>10 phút</strong>.
                            Vui lòng không chia sẻ mã này với bất kỳ ai.
                          </p>
                        </div>

                        <p style="margin:0;font-size:14px;color:#94a3b8;">
                          Nếu bạn không tạo tài khoản này, hãy bỏ qua email này.
                        </p>
                      </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                      <td style="background:#f8fafc;padding:20px 40px;
                                 border-top:1px solid #e2e8f0;text-align:center;">
                        <p style="margin:0;font-size:12px;color:#94a3b8;">
                          © 2025 WellCare4U · Mọi quyền được bảo lưu
                        </p>
                      </td>
                    </tr>

                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(firstName, digitBoxes.toString());
    }
}