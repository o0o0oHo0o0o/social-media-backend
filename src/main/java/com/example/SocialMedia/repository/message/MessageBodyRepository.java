package com.example.SocialMedia.repository.message;

import com.example.SocialMedia.model.messaging_model.MessageBodies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageBodyRepository extends JpaRepository<MessageBodies, Integer> {
    List<MessageBodies> findByMessageIDIn(List<Long> messageIds);
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query(value = "INSERT INTO Messaging.MessageBodies (MessageID, Content) VALUES (:msgId, :content)",
            nativeQuery = true)
    void insertBody(@org.springframework.data.repository.query.Param("msgId") Long messageId, @org.springframework.data.repository.query.Param("content") String content);
}
