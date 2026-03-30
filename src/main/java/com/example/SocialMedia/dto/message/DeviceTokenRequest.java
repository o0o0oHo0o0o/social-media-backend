package com.example.SocialMedia.dto.message;

import com.example.SocialMedia.constant.UserDeviceToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTokenRequest {
    private String deviceToken;
    private UserDeviceToken.DeviceType deviceType;
    private String deviceName;
}