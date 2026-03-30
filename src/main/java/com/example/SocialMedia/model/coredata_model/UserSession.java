package com.example.SocialMedia.model.coredata_model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "WebSocketSessions", schema = "CoreData")
@Data
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long ID;

    @Column(name = "SessionToken", nullable = false, unique = true)
    private String sessionToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Column(name = "IsActive", nullable = false)
    private boolean isActive;

    @Column(name = "ExpiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;
}
