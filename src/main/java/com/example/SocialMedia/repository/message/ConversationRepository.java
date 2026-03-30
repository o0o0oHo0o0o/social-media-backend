package com.example.SocialMedia.repository.message;

import com.example.SocialMedia.dto.projection.ConversationProjection;
import com.example.SocialMedia.model.messaging_model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    // 1. Gọi SP lấy danh sách hội thoại
    // Lưu ý: SQL Server của bạn đang dùng OFFSET (@PageNumber - 1), nên truyền PageNumber bắt đầu từ 1
    @Query(value = "EXEC Messaging.SP_GetUserConversations " +
            "@UserId = :userId, " +
            "@PageSize = :pageSize, " +
            "@PageNumber = :pageNumber, " +
            "@InboxType = :inboxType",
            nativeQuery = true)
    List<ConversationProjection> getUserConversations(
            @Param("userId") Integer userId,
            @Param("pageSize") Integer pageSize,
            @Param("pageNumber") Integer pageNumber,
            @Param("inboxType") String inboxType // Vẫn truyền String xuống DB
    );

    // 2. Gọi SP đánh dấu đã đọc (Vì đây là lệnh Update, cần @Modifying)
    @Modifying
    @Query(value = "EXEC Messaging.SP_MarkMessagesAsRead :userId, :conversationId, :lastMessageId", nativeQuery = true)
    void markMessagesAsRead(
            @Param("userId") Integer userId,
            @Param("conversationId") Integer conversationId,
            @Param("lastMessageId") Long lastMessageId
    );

    // 3. Gọi SP đếm số tin chưa đọc (cho trường hợp cần check nhanh)
    @Query(value = "EXEC Messaging.SP_GetUnreadMessageCount :userId, :conversationId", nativeQuery = true)
    Integer getUnreadMessageCount(
            @Param("userId") Integer userId,
            @Param("conversationId") Integer conversationId
    );

    // Trong ConversationMemberRepository thay vì ConversationRepository
    @Query("SELECT CASE WHEN COUNT(cm) > 0 THEN true ELSE false END " +
            "FROM ConversationMember cm " +
            "WHERE cm.conversation.conversationId = ?1 AND cm.user.id = ?2")
    boolean existsConversationMember(int conversationId, int userId);

    // ConversationRepository
    @Query(value = """
    SELECT TOP 1 c.*
    FROM Messaging.Conversations c
    JOIN Messaging.ConversationMembers cm1 ON c.ConversationID = cm1.ConversationID
    JOIN Messaging.ConversationMembers cm2 ON c.ConversationID = cm2.ConversationID
    WHERE c.IsGroupChat = 0
      AND cm1.UserID = :userId1
      AND cm2.UserID = :userId2
    ORDER BY c.ConversationID DESC
    """, nativeQuery = true)
    Optional<Conversation> findLatestPrivateConversation(
            @Param("userId1") int userId1,
            @Param("userId2") int userId2
    );
}