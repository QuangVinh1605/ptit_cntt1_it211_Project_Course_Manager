package org.example.course_manager.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetToken(String toEmail, String token) {
        String subject = "Yêu cầu đặt lại mật khẩu - Course Manager";
        String resetLink = "http://localhost:3000/reset-password?token=" + token; // Link frontend của bạn
        String message = "Xin chào,\n\n"
                + "Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n"
                + "Vui lòng nhập token sau để đặt lại mật khẩu (hoặc click vào link):\n\n"
                + "Token: " + token + "\n\n"
                + "Link: " + resetLink + "\n\n"
                + "Token có hiệu lực trong 1 giờ.\n"
                + "Nếu bạn không yêu cầu, vui lòng bỏ qua email này.\n\n"
                + "Trân trọng,\nCourse Manager Team";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("your_email@gmail.com"); // Nên dùng cùng email cấu hình

        try {
            mailSender.send(mailMessage);
            log.info("Gửi email reset password thành công tới {}", toEmail);
        } catch (Exception e) {
            log.error("Gửi email thất bại: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }
    public void sendPasswordResetTokenHtml(String toEmail, String token) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("Đặt lại mật khẩu - Course Manager");
        String htmlContent = "<h3>Yêu cầu đặt lại mật khẩu</h3>" +
                "<p>Token của bạn: <b>" + token + "</b></p>" +
                "<p>Hoặc click <a href='http://localhost:3000/reset-password?token=" + token + "'>vào đây</a> để đặt lại mật khẩu.</p>" +
                "<p>Token hết hạn sau 1 giờ.</p>";
        helper.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
}