package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.response.FollowUserResponse;
import com.example.SocialMedia.dto.response.ShortUserResponse;
import com.example.SocialMedia.service.social.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;
    @GetMapping("/{username}")
    public ResponseEntity<Boolean> getFollower(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        return ResponseEntity.ok(followService.getFollow(username, userDetails.getUsername()));
    }
    @PostMapping("/{username}")
    public ResponseEntity<ShortUserResponse> createFollow(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails // <--- Bảo mật
    ) {
        // Truyền username xuống service
        return ResponseEntity.ok(followService.createFollower(username, userDetails.getUsername()));
    }
    @DeleteMapping("/{username}")
    public ResponseEntity<ShortUserResponse> deleteFollow(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails // <--- Bảo mật
    ) {
        // Truyền username xuống service
        return ResponseEntity.ok(followService.deleteFollower(username, userDetails.getUsername()));
    }


    // FE đang gọi: GET /api/follows/{username}/followers
    @GetMapping("/{username}/followers")
    public ResponseEntity<Page<FollowUserResponse>> getFollowers(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(followService.getFollowers(username, page, size));
    }

    // FE đang gọi: GET /api/follows/{username}/following
    @GetMapping("/{username}/following")
    public ResponseEntity<Page<FollowUserResponse>> getFollowing(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(followService.getFollowing(username, page, size));
    }

    // BỔ SUNG: FE đang gọi GET /api/follows/{username}/friends
    @GetMapping("/{username}/friends")
    public ResponseEntity<Page<FollowUserResponse>> getFriends(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(followService.getFriends(username, page, size));
    }

}
