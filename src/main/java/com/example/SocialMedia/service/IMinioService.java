package com.example.SocialMedia.service;

import com.example.SocialMedia.dto.message.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IMinioService {
    FileUploadResponse uploadFile(MultipartFile file);
    String getFileUrl(String fileName);
    void deleteFile(String fileName);
    String resolvePublicUrl(String rawValue);
}
