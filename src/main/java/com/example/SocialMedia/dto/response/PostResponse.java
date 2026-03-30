package com.example.SocialMedia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private int id;
    private String content;
    private String postTopic;
    private String location;
    private ShortUserResponse user;
    private Integer interactableItemId;

    private Map<String, Long> reactionCounts;

    private int commentCount;
    private int shareCount;

    private List<PostMediaResponse> medias; // Dùng List thay vì Array[]

    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}