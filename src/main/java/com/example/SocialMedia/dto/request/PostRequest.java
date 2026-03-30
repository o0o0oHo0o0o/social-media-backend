package com.example.SocialMedia.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostRequest {
    private Integer postId;
    private String content;
    @JsonIgnore
    private MultipartFile[] medias;

    private int[] deleteMedia;

    private String postTopic;
    private String location;

    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}