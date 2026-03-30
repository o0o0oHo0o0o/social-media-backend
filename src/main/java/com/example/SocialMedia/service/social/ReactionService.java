package com.example.SocialMedia.service.social;

import com.example.SocialMedia.dto.response.ReactionCountResponse;
import com.example.SocialMedia.dto.request.ReactionRequest;

import java.util.List;


public interface ReactionService {
    // Sửa userId thành Integer (có thể null)
    List<ReactionCountResponse> getReactionCount(Integer userId, int interactableItemId);

    String addReaction(String username, ReactionRequest request);

    // Thêm username để check quyền
    void deleteReaction(String username, int id);

    List<ReactionCountResponse> getReactionStatsSmart(Integer userId, int targetId, String targetType);
}
