package com.example.SocialMedia.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaDto {
    private String url;
    private String type;
    private String fileName;
    private Integer fileSize;
}