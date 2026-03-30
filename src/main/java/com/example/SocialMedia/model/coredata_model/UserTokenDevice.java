package com.example.SocialMedia.model.coredata_model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "UserDeviceToken", schema = "CoreData")
public class UserTokenDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TokenID")
    private long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Column(name = "DeviceToken", nullable = false)
    private String deviceToken;

    @Column(name = "isActive", nullable = false)
    private boolean isActive;

    @Column(name = "DeviceType")
    private String deviceType;

    @Column(name = "LastUsedAt")
    private LocalDateTime lastUsedAt;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;
}
