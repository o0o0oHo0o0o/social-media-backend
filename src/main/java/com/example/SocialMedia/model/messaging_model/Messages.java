package com.example.SocialMedia.model.messaging_model;

import com.example.SocialMedia.constant.MessageType;
import com.example.SocialMedia.model.coredata_model.InteractableItems;
import com.example.SocialMedia.model.coredata_model.Reaction;
import com.example.SocialMedia.model.coredata_model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyGroup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Messages", schema = "Messaging")
@Getter
@Setter
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MessageID")
    private long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ConversationID", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SenderID", nullable = false)
    private User sender;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InteractableItemID")
    private InteractableItems interactableItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReplyToMessageID")
    private Messages replyMessage;

    @OneToMany(mappedBy = "replyMessage", fetch = FetchType.LAZY)
    private Set<Messages> replies;

    @Column(name = "SequenceNumber", nullable = false)
    private Long sequenceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "MessageType")
    private MessageType messageType;

    @Column(name = "SentAt", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "isDeleted")
    private boolean isDeleted;

    @OneToOne(
            mappedBy = "message",
            cascade = CascadeType.ALL,
            optional = false
    )
    @LazyGroup("messageBody")
    private MessageBodies messageBody;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY)
    private Set<MessageMedia> messageMedia;

    @OneToMany(mappedBy = "lastReadMessage", fetch = FetchType.LAZY)
    private Set<ConversationMember> seenByMembers;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "InteractableItemID", referencedColumnName = "InteractableItemID")
    private List<Reaction> reactions;

    public void setMessageBody(MessageBodies messageBody) {
        if (messageBody == null) {
            if (this.messageBody != null) {
                this.messageBody.setMessage(null);
            }
        } else {
            messageBody.setMessage(this);
        }
        this.messageBody = messageBody;
    }
}
