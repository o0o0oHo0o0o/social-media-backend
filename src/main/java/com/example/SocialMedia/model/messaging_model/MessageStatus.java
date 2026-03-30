package com.example.SocialMedia.model.messaging_model;

import com.example.SocialMedia.keys.MessageStatusID;
import com.example.SocialMedia.model.coredata_model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "MessageStatus", schema = "Messaging")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatus {

    @EmbeddedId
    private MessageStatusID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageID")
    @JoinColumn(name = "MessageID", nullable = false)
    private Messages message;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userID")
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false, length = 20)
    private MessageStatusEnum status = MessageStatusEnum.SENT;

    @Column(name = "DeliveredAt")
    private LocalDateTime deliveredAt;

    @Column(name = "ReadAt")
    private LocalDateTime readAt;

    public enum MessageStatusEnum {
        SENT,
        DELIVERED,
        READ
    }
}