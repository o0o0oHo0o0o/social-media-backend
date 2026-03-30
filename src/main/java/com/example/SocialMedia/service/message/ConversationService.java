package com.example.SocialMedia.service.message;

import com.example.SocialMedia.dto.response.ReactionResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ConversationService {
    void updateMemberNickname(Integer conversationId, Integer userId, String nickname, String requesterUsername);

    @Transactional
    void updateConversationName(int conversationId, String newName, String requesterUsername);

    @Transactional
    String updateConversationAvatar(int conversationId, MultipartFile file, String requesterUsername);

    @Transactional
    List<ReactionResponse> getReactionDetails(Long messageId);
}
