package com.example.SocialMedia.dto.response;

import com.example.SocialMedia.model.coredata_model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SenderDto {
    private Integer userId;
    private String nickname;
    private String fullName;
    private String avatarUrl;

    // Helper map từ User Entity
    public static SenderDto fromUser(User user, String nicknameInGroup) {
        return SenderDto.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .nickname(nicknameInGroup != null ? nicknameInGroup : user.getFullName())
                .avatarUrl(user.getProfilePictureURL())
                .build();
    }
}