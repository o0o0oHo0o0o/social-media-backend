package com.example.SocialMedia.serviceImpl.social;

import com.example.SocialMedia.service.social.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageServiceImpl implements StorageService {
    private final Path uploadDirectory = Paths.get("backend/src/main/resources/static/uploads");
    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path filePath = uploadDirectory.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        return "http://localhost:8080/uploads/" + fileName;// Save the relative path to the database
    }
    @Override
    public void deleteImage(String fileName) throws IOException {
        System.out.println("Delete directory is: " + uploadDirectory.toAbsolutePath());
        Path filePath = uploadDirectory.resolve(fileName.substring(fileName.lastIndexOf('/') + 1));
        Files.delete(filePath);
    }
}
