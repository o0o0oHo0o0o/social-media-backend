package com.example.SocialMedia.serviceImpl.social;

import com.example.SocialMedia.constant.ReactionType;
import com.example.SocialMedia.dto.request.ReactionRequest;
import com.example.SocialMedia.dto.response.ReactionCountResponse;
import com.example.SocialMedia.dto.response.ReactionStat; // Đảm bảo đã có Interface này từ bài trước
import com.example.SocialMedia.exception.ResourceNotFound.ResourceNotFoundException;
import com.example.SocialMedia.exception.ResourceNotFound.UserNotFoundException;
import com.example.SocialMedia.model.coredata_model.*;
import com.example.SocialMedia.repository.*;
import com.example.SocialMedia.service.social.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * Lấy danh sách đếm reaction (Ví dụ: LIKE: 5, LOVE: 2)
     * Và đánh dấu xem user hiện tại đã thả cái nào chưa
     */
    @Override
    public List<ReactionCountResponse> getReactionCount(Integer userId, int interactableItemId) {
        // 1. Lấy thống kê số lượng (Group By Type)
        // Lưu ý: Cần đảm bảo ReactionRepository có hàm trả về List<ReactionStat> hoặc List<Object[]> chuẩn
        List<ReactionStat> stats = reactionRepository.countReactionsByInteractableItemId(interactableItemId);

        // 2. Kiểm tra xem User hiện tại đã thả tim chưa (nếu có userId)
        ReactionType userReactionType = null;
        if (userId != null) {
            Optional<Reaction> userReaction = reactionRepository.findByInteractableItems_InteractableItemIdAndUser_Id(interactableItemId, userId);
            if (userReaction.isPresent()) {
                userReactionType = userReaction.get().getReactionType();
            }
        }

        final ReactionType finalUserReactionType = userReactionType;

        // 3. Map sang Response
        // Nếu stats rỗng (chưa ai like), trả về list rỗng hoặc logic tùy ý
        if (stats == null || stats.isEmpty()) {
            return new ArrayList<>();
        }

        return stats.stream().map(stat -> {
            ReactionCountResponse response = new ReactionCountResponse();
            response.setReactionType(stat.getReactionType().name()); // Enum to String
            response.setReactionCount(Math.toIntExact(stat.getReactionCount())); // Long to Int

            // Check xem user có thả đúng loại này không
            boolean isReacted = finalUserReactionType != null && finalUserReactionType == stat.getReactionType();
            response.setHasUserReaction(isReacted);

            return response;
        }).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public String addReaction(String username, ReactionRequest request) {

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));


        InteractableItems item = switch (request.getTargetType()) {
            case POST -> {
                Post post = postRepository.findById(request.getTargetId())
                        .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
                yield post.getInteractableItem();
            }
            case COMMENT -> {
                Comment comment = commentRepository.findById(request.getTargetId())
                        .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
                yield comment.getOwnInteractableItem();
            }
            default -> throw new IllegalArgumentException("Invalid target type");
        };

        if (item == null) throw new ResourceNotFoundException("Item interaction not initialized");

        // 3. Xử lý Logic Toggle
        Optional<Reaction> reactionOpt = reactionRepository
                .findReactionByInteractableItemsAndUser(item, user);

        String action = "";
        LocalDateTime now = LocalDateTime.now();

        if (reactionOpt.isPresent()) {
            Reaction reaction = reactionOpt.get();
            if (reaction.getReactionType() == request.getReactionType()) {
                // Bấm trùng -> Xóa
                reactionRepository.delete(reaction);
                action = "REMOVED";
            } else {
                // Bấm khác -> Đổi
                reaction.setReactionType(request.getReactionType());
                reaction.setReactedLocalDateTime(now);
                reactionRepository.save(reaction);
                action = "UPDATED";
            }
        } else {
            // Chưa có -> Tạo mới
            if (request.getReactionType() != null) {
                Reaction newReaction = new Reaction();
                newReaction.setUser(user);
                newReaction.setInteractableItems(item);
                newReaction.setReactionType(request.getReactionType());
                newReaction.setReactedLocalDateTime(now);
                reactionRepository.save(newReaction);
                action = "ADDED";
            }
        }
        return action;
    }

    @Override
    public void deleteReaction(String username, int id) {
        Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found: " + id));

        // CHECK QUYỀN: Chỉ chủ sở hữu mới được xóa
        if (!reaction.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền xóa reaction này");
        }

        reactionRepository.delete(reaction);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ReactionCountResponse> getReactionStatsSmart(Integer userId, int targetId, String targetType) {
        int realInteractableId = targetId;

        if ("POST".equalsIgnoreCase(targetType)) {
            Post post = postRepository.findById(targetId).orElse(null);

            if (post != null && post.getInteractableItem() != null) {
                realInteractableId = post.getInteractableItem().getInteractableItemId();
                return getReactionCount(userId, realInteractableId);
            } else {
                // fallback: treat targetId as already an interactableItemId
                // call getReactionCount directly (it will return empty list if none)
                return getReactionCount(userId, targetId);
            }
        } else if ("COMMENT".equalsIgnoreCase(targetType)) {
            Comment comment = commentRepository.findById(targetId).orElse(null);

            if (comment != null && comment.getOwnInteractableItem() != null) {
                realInteractableId = comment.getOwnInteractableItem().getInteractableItemId();
                return getReactionCount(userId, realInteractableId);
            } else {
                // fallback to treat targetId as interactableItemId
                return getReactionCount(userId, targetId);
            }
        }

        // If unknown type, try safest: treat as interactable id
        return getReactionCount(userId, targetId);
    }
}