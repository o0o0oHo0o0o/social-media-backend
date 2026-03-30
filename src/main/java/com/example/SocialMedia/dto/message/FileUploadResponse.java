package com.example.SocialMedia.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private Integer mediaId;
    private String mediaName;
    private String mediaUrl;
    private String thumbnailUrl;
    private String fileName;
    private Integer fileSize;
    private String mediaType;
}