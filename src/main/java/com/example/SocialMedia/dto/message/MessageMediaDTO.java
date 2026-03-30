package com.example.SocialMedia.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageMediaDTO {
    private Integer mediaId;
    private String mediaUrl;
    private String mediaType; // IMAGE, VIDEO, AUDIO, FILE
    private String fileName;
    private Integer fileSize;
    private String thumbnailUrl;
}
