package com.example.SocialMedia.mapper;

import com.example.SocialMedia.dto.UserProfileDto;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.service.IMinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final IMinioService minioService;

    public UserProfileDto toUserProfileDto(User user) {
        if (user == null) {
            return null;
        }

        // 1. Lấy và biến đổi Avatar URL
        String rawAvatar = user.getProfilePictureURL();
        String finalAvatarUrl = minioService.resolvePublicUrl(rawAvatar);

        if (rawAvatar != null && !rawAvatar.isBlank()) {
            if (rawAvatar.startsWith("http")) {
                finalAvatarUrl = rawAvatar; // Ảnh từ Google/Facebook
            } else {
                finalAvatarUrl = minioService.getFileUrl(rawAvatar); // Tạo Presigned URL từ MinIO
            }
        }

        // 2. Trả về DTO hoàn chỉnh
        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getUsername(),
                user.getFullName(),
                finalAvatarUrl,
                user.getAuthProvider(),
                user.getCreatedLocalDateTime()
        );
    }
}