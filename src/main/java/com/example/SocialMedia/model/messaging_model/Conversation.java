package com.example.SocialMedia.model.messaging_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name =  "Conversations", schema = "Messaging")
@Getter
@Setter
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ConversationID")
    private int conversationId;

    @Column(name = "GroupImageFile")
    private String groupImageFile;

    @Column(name = "ConversationName")
    private String conversationName;

    @Column(name = "IsGroupChat", nullable = false)
    private boolean isGroupChat;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdLocalDateTime;

    @Column(name = "LastMessageID")
    private long lastMessageID;

    @Column(name = "CreatedByUserID")
    private int createdByUserID;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Messages> messages;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ConversationMember> members;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PinnedMessages> pinnedMessages;

    @PrePersist
    protected void onCreate() {
        if (this.createdLocalDateTime == null) {
            this.createdLocalDateTime = LocalDateTime.now();
        }
    }
}
