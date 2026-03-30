package com.example.SocialMedia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupCandidateResponse {
    private Integer userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private boolean isMutualFollow;
    private boolean eligibleForGroup;
    private String eligibilityReason;
}