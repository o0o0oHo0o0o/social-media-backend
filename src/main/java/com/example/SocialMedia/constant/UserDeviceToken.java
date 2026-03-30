package com.example.SocialMedia.constant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceToken {

    public enum DeviceType {
        ANDROID,

        IOS,

        WEB
    }
    private Integer id;
    private Integer userId;
    private String deviceToken;
    private DeviceType deviceType;
    private String deviceName;
    private LocalDateTime lastUsedAt;
}