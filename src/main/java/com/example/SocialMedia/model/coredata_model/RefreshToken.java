package com.example.SocialMedia.model.coredata_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "RefreshTokens", schema = "CoreData")
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TokenID")
    private int tokenID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User user;

    @Column(name = "Token", unique = true, nullable = false)
    private String token;

    @Column(name = "ExpiryDate")
    private LocalDateTime expiryDate;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
}
