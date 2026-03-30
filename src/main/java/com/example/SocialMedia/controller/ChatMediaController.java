package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.MemberDto;
import com.example.SocialMedia.dto.file.FileDto;
import com.example.SocialMedia.dto.file.PhotoDto;
import com.example.SocialMedia.service.message.ChatMediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // ✅ 1. Import cái này
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatMediaController {

    private final ChatMediaService chatMediaService;

    @GetMapping("/{conversationId}/photos")
    public ResponseEntity<?> getConversationPhotos(
            @PathVariable int conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<PhotoDto> photos = chatMediaService.getPhotos(conversationId, page, size);
            return ResponseEntity.ok(photos);
        } catch (Exception e) {
            log.error("Error getting photos for conversationId: {}", conversationId, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal Server Error", "message", e.getMessage()));
        }
    }

    @GetMapping("/{conversationId}/files")
    public ResponseEntity<?> getConversationFiles(
            @PathVariable int conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Page<FileDto> files = chatMediaService.getFiles(conversationId, page, size);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("Error getting files for conversationId: {}", conversationId, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal Server Error", "message", e.getMessage()));
        }
    }

    @GetMapping("/{conversationId}/members/details")
    public ResponseEntity<?> getConversationMembers(
            @PathVariable int conversationId
    ) {
        try {
            List<MemberDto> members = chatMediaService.getMembers(conversationId);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            log.error("Error getting members for conversationId: {}", conversationId, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal Server Error", "message", e.getMessage()));
        }
    }
}