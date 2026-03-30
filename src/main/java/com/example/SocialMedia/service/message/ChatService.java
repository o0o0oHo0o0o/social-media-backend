package com.example.SocialMedia.service.message;

import com.example.SocialMedia.constant.InboxType;
import com.example.SocialMedia.dto.request.CreateConversationRequest;
import com.example.SocialMedia.dto.request.CreatePrivateChatRequest;
import com.example.SocialMedia.dto.request.ReactionRequest;
import com.example.SocialMedia.dto.request.SendMessageRequest;
import com.example.SocialMedia.dto.response.ConversationResponse;
import com.example.SocialMedia.dto.response.GroupCandidateResponse;
import com.example.SocialMedia.dto.response.MessageResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatService {
    MessageResponse sendMessage(String username, SendMessageRequest request, List<MultipartFile> files);
    List<ConversationResponse> getUserConversations(String username, int page, int size, InboxType inboxType);
    List<MessageResponse> getMessages(String username, int conversationId, int page, int size);

    @Transactional
    String reactToMessage(String username, ReactionRequest request);

    ConversationResponse createConversation(String username, CreateConversationRequest request);

    ConversationResponse createPrivateConversation(String username, CreatePrivateChatRequest request);
    List<GroupCandidateResponse> searchGroupCandidates(String username, String keyword);
}
