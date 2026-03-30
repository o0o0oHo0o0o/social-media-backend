package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.request.ReactionRequest;
import com.example.SocialMedia.dto.response.ReactionCountResponse;
import com.example.SocialMedia.service.social.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    // Lấy thống kê reaction của 1 item (kèm trạng thái user hiện tại đã like chưa)
    // Ví dụ: GET /api/reactions/stats?targetId=10&userId=5
    @GetMapping("/stats/{targetId}")
    public ResponseEntity<List<ReactionCountResponse>> getReactionStats(
            @PathVariable int targetId,
            @RequestParam(required = false, defaultValue = "POST") String targetType, // Mặc định là POST cho dễ test
            @RequestParam(required = false) Integer userId
    ) {
        // Gọi Service xử lý chuyển đổi ID
        return ResponseEntity.ok(reactionService.getReactionStatsSmart(userId, targetId, targetType));
    }

    // Toggle Reaction (Add/Remove/Update)
    // Đường dẫn: POST /api/reactions (Bỏ chữ /reactions thừa)
    @PostMapping
    public ResponseEntity<?> addReaction(
            @RequestBody ReactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 1. Gọi Service
        String action = reactionService.addReaction(userDetails.getUsername(), request);

        // 2. Trả về kết quả
        return ResponseEntity.ok(Map.of(
                "message", "Success",
                "action", action,
                "targetId", request.getTargetId()
        ));
    }

    // Xóa reaction theo ID (Cần cẩn thận, thường dùng logic toggle ở trên là đủ)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReaction(
            @PathVariable int id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        reactionService.deleteReaction(userDetails.getUsername(), id);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }
}