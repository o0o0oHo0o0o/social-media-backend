package com.example.SocialMedia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMediaResponse {
    private int id;
    private String mediaURL;  // Link đã ký (Presigned URL)
    private String mediaType; // IMAGE, VIDEO...
    private String fileName;  // Tên file gốc trong MinIO (để tiện trace)
    private int sortOrder;
}