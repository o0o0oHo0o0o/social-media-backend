package com.example.SocialMedia.model.messaging_model;

import com.example.SocialMedia.keys.ConversationMembersID;
import com.example.SocialMedia.model.coredata_model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "ConversationMembers", schema = "Messaging")
@Getter
@Setter
public class ConversationMember {
    @EmbeddedId
    private ConversationMembersID conversationMembersID;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conversationID")
    @JoinColumn(name = "conversationID", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userID")
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @Column(name = "Nickname")
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LastReadMessageID")
    private Messages lastReadMessage;

    @Column(name = "Role")  // Thêm dòng này
    private String role;

    @Column(name = "MutedUntil")
    private LocalDateTime  mutedUntil;

    @Column(name = "JoinedAt", nullable = false)
    private LocalDateTime joinedLocalDateTime;
}
