package com.example.SocialMedia.serviceImpl.authImpl;

import com.example.SocialMedia.service.auth.SmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct; // <-- 1. Import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String twilioPhoneNumber;

    @Value("${twilio.messaging-service-sid}")
    private String messagingServiceSid;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    @Override
    public void sendOtpSms(String phoneNumber, String otp) {
        try {
            String formattedPhoneNumber = formatPhoneNumber(phoneNumber);

            Message message = Message.creator(
                    new PhoneNumber(formattedPhoneNumber),
                    messagingServiceSid,
                    "Mã xác thực OTP của bạn là: " + otp + ". Mã này sẽ hết hạn sau 5 phút."
            ).create();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi gửi SMS: " + e.getMessage());
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        }
        if (phoneNumber.startsWith("0")) {
            return "+84" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}