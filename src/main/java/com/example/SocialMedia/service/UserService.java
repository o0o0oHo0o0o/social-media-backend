package com.example.SocialMedia.service;

import com.example.SocialMedia.constant.OtpChannel;
import com.example.SocialMedia.dto.UserProfileDto;
import com.example.SocialMedia.dto.response.ShortUserResponse;
import com.example.SocialMedia.model.coredata_model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    String generateUniqueUsername(String identifier, OtpChannel channel);

    Optional<User> findByEmail(String email);

    User saveUser(User user);

    List<ShortUserResponse> getUserByKeyword(String keyword, Pageable pageable);

    String updateAvatar(String username, MultipartFile file);
    public UserProfileDto getUserProfile(String username);
}
