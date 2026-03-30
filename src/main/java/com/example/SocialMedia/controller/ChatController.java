package com.example.SocialMedia.controller;

import com.example.SocialMedia.constant.InboxType;
import com.example.SocialMedia.dto.message.ConversationMemberDTO;
import com.example.SocialMedia.dto.message.WebSocketTokenResponse;
import com.example.SocialMedia.dto.request.*;
import com.example.SocialMedia.dto.response.ConversationResponse;
import com.example.SocialMedia.dto.response.GroupCandidateResponse;
import com.example.SocialMedia.dto.response.MessageResponse;
import com.example.SocialMedia.dto.response.ReactionResponse;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.repository.UserRepository;
import com.example.SocialMedia.service.message.ChatService;
import com.example.SocialMedia.service.message.ConversationService;
import com.example.SocialMedia.service.message.MessageStatusService;
import com.example.SocialMedia.service.message.WebSocketSessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MessageStatusService messageStatusService;
    private final WebSocketSessionService webSocketSessionService;
    private final UserRepository userRepository;
    private final ConversationService conversationService;

    // 1. Endpoint Thả Reaction (Toggle: Add/Remove/Update)
    @PostMapping("/reactions")
    public ResponseEntity<?> reactToMessage(
            @RequestBody ReactionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String action = chatService.reactToMessage(userDetails.getUsername(), request);

            return ResponseEntity.ok(Map.of(
                    "message", "Reaction updated successfully",
                    "action", action,
                    "targetId", request.getTargetId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi server: " + e.getMessage()));
        }
    }
    // 2. Endpoint Lấy danh sách chi tiết người thả tim (Lazy Loading)
    // Dùng khi user click vào icon reaction để xem "Ai đã thả tim?"
    @GetMapping("/messages/{messageId}/reactions")
    public ResponseEntity<?> getMessageReactionDetails(@PathVariable Long messageId) {
        try {
            List<ReactionResponse> details = conversationService.getReactionDetails(messageId);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    // 1. Cập nhật ảnh đại diện nhóm (Conversation Avatar)
    @PutMapping(value = "/conversations/{conversationId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateConversationAvatar(
            @PathVariable int conversationId,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "File ảnh không được để trống"));
            }

            // Gọi Service
            String newAvatarUrl = conversationService.updateConversationAvatar(
                    conversationId,
                    file,
                    userDetails.getUsername()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật ảnh nhóm thành công",
                    "data", newAvatarUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lỗi: " + e.getMessage()));
        }
    }
    // 2. Controller endpoint - Thêm vào ConversationController hoặc MessagingController
    @PutMapping("/{conversationId}/nickname")
    public ResponseEntity<?> updateNickname(
            @PathVariable Integer conversationId,
            @RequestBody ConversationMemberDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Lấy targetUserId từ request body - người muốn đổi nickname
            Integer targetUserId = request.getUserId();

            if (targetUserId == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "userId is required"
                ));
            }

            conversationService.updateMemberNickname(
                    conversationId,
                    targetUserId,
                    request.getNickname(),
                    userDetails.getUsername()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Cập nhật biệt danh thành công",
                    "data", request.getNickname()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }
    // 3. Đổi tên nhóm (Conversation Name)
    @PutMapping("/{conversationId}/name")
    public ResponseEntity<?> updateConversationName(
            @PathVariable int conversationId,
            @RequestBody Map<String, String> payload, // Nhận JSON: {"conversationName": "Tên Nhóm Mới"}
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String newName = payload.get("conversationName");

            // Validate cơ bản
            if (newName == null || newName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Tên nhóm không được để trống"));
            }

            // Gọi Service xử lý
            conversationService.updateConversationName(conversationId, newName, userDetails.getUsername());

            return ResponseEntity.ok(Map.of(
                    "message", "Đổi tên nhóm thành công",
                    "data", newName
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi server: " + e.getMessage()));
        }
    }

    // 1. Gửi tin nhắn (Text + File)
    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SendTo("/topic/chat.{conversationId}")
    public ResponseEntity<MessageResponse> sendMessage(
            @RequestPart("data") String messageJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {

        System.out.println("[ChatController] sendMessage called");
        System.out.println("[ChatController] userDetails: " + (userDetails != null ? userDetails.getUsername() : "NULL"));
        System.out.println("[ChatController] messageJson: " + messageJson);
        System.out.println("[ChatController] files: " + (files != null ? files.size() : 0));
        if (files != null) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile f = files.get(i);
                System.out.println("[ChatController]   [" + i + "] " + f.getOriginalFilename() + " - " + f.getSize() + " bytes");
            }
        }

        SendMessageRequest request = new ObjectMapper().readValue(messageJson, SendMessageRequest.class);
        System.out.println("[ChatController] Request parsed: " + request);

        assert userDetails != null;
        MessageResponse response = chatService.sendMessage(userDetails.getUsername(), request, files);
        return ResponseEntity.ok(response);
    }

    // 2. Đánh dấu đã đọc (Read Receipt)
    @PostMapping("/read")
    public ResponseEntity<Void> markAsRead(
            @RequestBody MarkReadRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        messageStatusService.markAsRead(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/ws-token")
    public ResponseEntity<WebSocketTokenResponse> issueToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return ResponseEntity.status(401).build();

        String username = auth.getName();
        var user = userRepository.findByUserNameWithRoles(username).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        var token = webSocketSessionService.generateToken(user);
        return ResponseEntity.ok(token);
    }
    // ChatController.java
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> listConversations(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "ALL") InboxType inboxType) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = auth.getName();
        var data = chatService.getUserConversations(username, page, size, inboxType);
        return ResponseEntity.ok(data);
    }
    @PostMapping("/conversations")
    public ResponseEntity<ConversationResponse> createConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateConversationRequest request) {

        ConversationResponse response =
                chatService.createConversation(userDetails.getUsername(), request);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/private")
    public ResponseEntity<ConversationResponse> createPrivateChat(
            @AuthenticationPrincipal UserDetails userDetails, // Lấy user đang đăng nhập
            @RequestBody CreatePrivateChatRequest request) {
        // Gọi service xử lý
        return ResponseEntity.ok(chatService.createPrivateConversation(userDetails.getUsername(), request));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> listMessages(
            @PathVariable int conversationId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String username = auth.getName();
        var data = chatService.getMessages(username, conversationId, page, size);
        return ResponseEntity.ok(data);
    }
    @GetMapping("/group/candidates")
    public ResponseEntity<List<GroupCandidateResponse>> searchGroupCandidates(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "") String keyword) {

        List<GroupCandidateResponse> candidates = chatService.searchGroupCandidates(currentUser.getUsername(), keyword);
        return ResponseEntity.ok(candidates);
    }
}