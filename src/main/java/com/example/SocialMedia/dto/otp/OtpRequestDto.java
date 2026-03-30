package com.example.SocialMedia.dto.otp;

import com.example.SocialMedia.constant.OtpChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpRequestDto {
    private String identifier;
    private OtpChannel otpChannel;
}
