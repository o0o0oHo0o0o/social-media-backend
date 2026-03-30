package com.example.SocialMedia.model.messaging_model;

import com.example.SocialMedia.keys.PinnedMessagesID;
import com.example.SocialMedia.model.coredata_model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PinnedMessages", schema = "Messaging")
@Getter
@Setter
public class PinnedMessages {
    @EmbeddedId
    private PinnedMessagesID pinnedMessagesID;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId("messageID")
    @JoinColumn(name = "MessageID")
    private Messages message;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conversationID")
    @JoinColumn(name = "ConversationID")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PinnedByUserID")
    private User user;

    @Column(name = "PinnedAt")
    private LocalDateTime pinnedAt;
}
