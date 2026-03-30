package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.response.AvatarUpdateResponse;
import com.example.SocialMedia.dto.response.ShortUserResponse;
import com.example.SocialMedia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<ShortUserResponse>> getUserByKeyword(
            @PathVariable String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getUserByKeyword(keyword, pageable));
    }
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AvatarUpdateResponse> updateAvatar(
            @AuthenticationPrincipal UserDetails userDetails, // Lấy user đang đăng nhập từ Token
            @RequestParam("file") MultipartFile file) { // FE phải gửi field name là "file"

        String username = userDetails.getUsername();
        String newUrl = userService.updateAvatar(username, file);

        return ResponseEntity.ok(new AvatarUpdateResponse(newUrl));
    }
}
