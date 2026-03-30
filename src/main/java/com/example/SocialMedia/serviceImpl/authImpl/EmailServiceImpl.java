package com.example.SocialMedia.serviceImpl.authImpl;

import com.example.SocialMedia.service.auth.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static com.example.SocialMedia.serviceImpl.authImpl.OtpServiceImpl.OTP_EXPIRY_MINUTES;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;
    
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            String htmlBody = buildOtpEmailBody(otp);
            helper.setTo(to);
            helper.setFrom(fromEmail);
            helper.setSubject("Otp Verification");
            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }

    @Override
    public String buildOtpEmailBody(String otp) {
        try {
            String template = new String(
                    Objects.requireNonNull(getClass().getResourceAsStream("/templates/otp_template.html")).readAllBytes(),
                    StandardCharsets.UTF_8
            );
            template = template.replace("{{OTP}}", otp);
            template = template.replace("{{EXPIRY}}", String.valueOf(OTP_EXPIRY_MINUTES));

            return template;
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc template email", e);
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(String to, String name) {
        CompletableFuture.completedFuture("");
    }


}
