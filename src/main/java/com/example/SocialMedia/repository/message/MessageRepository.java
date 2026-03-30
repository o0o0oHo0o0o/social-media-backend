package com.example.SocialMedia.repository.message;

import com.example.SocialMedia.model.messaging_model.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Messages, Long> {

    @Query("SELECT m FROM Messages m " +
            "LEFT JOIN FETCH m.messageBody " +
            "WHERE m.conversation.conversationId = :conversationId " +
            "AND m.isDeleted = false " +
            "ORDER BY m.sequenceNumber DESC")
    Page<Messages> findByConversationIdOrderBySequenceNumberDesc(
            @Param("conversationId") Integer conversationId,
            Pageable pageable
    );

    @Query("SELECT m FROM Messages m WHERE m.conversation.conversationId = :conversationId " +
            "AND m.sequenceNumber > :afterSequence AND m.isDeleted = false " +
            "ORDER BY m.sequenceNumber ASC")
    List<Messages> findNewMessages(
            @Param("conversationId") Integer conversationId,
            @Param("afterSequence") Long afterSequence
    );
    @Procedure(procedureName = "Messaging.SP_GetUnreadMessageCount")
    Integer getUnreadMessageCount(
            @Param("userId") Integer userId,
            @Param("conversationId") Integer conversationId
    );
    Page<Messages> findByConversation_ConversationId(int conversationId, Pageable pageable);

    @Query("""
        SELECT DISTINCT m FROM Messages m
        JOIN m.messageMedia mm
        WHERE m.conversation.conversationId = :conversationId
        AND mm.MediaType = 'IMAGE'
        AND m.isDeleted = false
        ORDER BY m.sentAt DESC
    """)
    Page<Messages> findPhotos(int conversationId, Pageable pageable);

    @Query("""
        SELECT DISTINCT m FROM Messages m
        JOIN m.messageMedia mm
        WHERE m.conversation.conversationId = :conversationId
        AND mm.MediaType = 'FILE'
        AND m.isDeleted = false
        ORDER BY m.sentAt DESC
    """)
    Page<Messages> findFiles(int conversationId, Pageable pageable);

}