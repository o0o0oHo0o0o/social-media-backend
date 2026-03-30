package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.response.PostResponse;
import com.example.SocialMedia.service.social.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    // SỬA: Bỏ {userId} trên URL, lấy từ Token đăng nhập
    @GetMapping("/home")
    public ResponseEntity<List<PostResponse>> getHome(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable
    ) {
        return ResponseEntity.ok(feedService.getHome(userDetails.getUsername(), pageable));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<PostResponse>> getPopular(Pageable pageable) {
        return ResponseEntity.ok(feedService.getPopular(pageable));
    }

    @GetMapping("/discussion")
    public ResponseEntity<List<PostResponse>> getDiscussion(Pageable pageable) {
        return ResponseEntity.ok(feedService.getDiscussion(pageable));
    }
}