package com.example.SocialMedia.controller;

import com.example.SocialMedia.dto.UserProfileDto;
import com.example.SocialMedia.mapper.UserMapper;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String username) {
        Optional<User> user = userRepository.findByUserName(username);
        return user.map(value -> ResponseEntity.ok(userMapper.toUserProfileDto(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
