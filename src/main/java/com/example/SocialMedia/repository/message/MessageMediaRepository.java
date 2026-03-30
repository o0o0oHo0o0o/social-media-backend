package com.example.SocialMedia.repository.message;

import com.example.SocialMedia.model.messaging_model.MessageMedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageMediaRepository extends JpaRepository<MessageMedia, Integer> {

    List<MessageMedia> findByMessage_MessageIdIn(List<Long> messageIds);

    @Query("""
       SELECT mm FROM MessageMedia mm
       WHERE mm.message.conversation.conversationId = :conversationId
       AND mm.MediaType IN :mediaTypes
       ORDER BY mm.message.sentAt DESC
       """)
    Page<MessageMedia> findMediaByConversationAndTypes(
            @Param("conversationId") Integer conversationId,
            @Param("mediaTypes") List<String> mediaTypes,
            Pageable pageable
    );
}