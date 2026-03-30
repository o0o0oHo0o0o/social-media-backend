package com.example.SocialMedia.repository.message;

import com.example.SocialMedia.keys.MessageStatusID;
import com.example.SocialMedia.model.messaging_model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, MessageStatusID> {

    List<MessageStatus> findByMessage_MessageId(long message_messageId);

    @Query("SELECT ms FROM MessageStatus ms WHERE ms.message.conversation.conversationId = :conversationId " +
            "AND ms.user.id = :userId AND ms.status != 'READ'")
    List<MessageStatus> findUnreadMessageStatuses(
            @Param("conversationId") Integer conversationId,
            @Param("userId") Integer userId
    );

    @Modifying
    @Query("UPDATE MessageStatus ms SET ms.status = 'DELIVERED', ms.deliveredAt = :now " +
            "WHERE ms.user.id = :userId AND ms.message.messageId IN :messageIds AND ms.status = 'SENT'")
    void markAsDelivered(
            @Param("userId") Integer userId,
            @Param("messageIds") List<Long> messageIds,
            @Param("now") LocalDateTime now
    );

    @Procedure(procedureName = "Messaging.SP_MarkMessagesAsRead")
    void markAsRead(
            @Param("UserID") Integer userId,
            @Param("ConversationID") Integer conversationId,
            @Param("LastMessageID") Long lastMessageId
    );

    List<MessageStatus> findByMessage_MessageIdInAndUser_Id(List<Long> messageIds, Integer userId);

    Collection<MessageStatus> findByMessage_MessageIdIn(List<Long> ids);
}
