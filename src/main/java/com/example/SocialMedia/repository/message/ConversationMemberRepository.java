package com.example.SocialMedia.repository.message;

import com.example.SocialMedia.model.messaging_model.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, Integer> {
    Optional<ConversationMember> findByConversation_ConversationIdAndUser_Id(
            Integer conversationId,
            Integer userId
    );

    Boolean existsByConversation_ConversationIdAndUser_Id(int conversationId, int userId);

    List<ConversationMember> findByConversation_ConversationId(int conversationId);

    Boolean existsByConversation_ConversationIdAndUser_UserName(int conversationId, String username);

    Optional<ConversationMember> findByConversation_ConversationIdAndUser_UserName(Integer conversationId, String requesterUsername);
}


