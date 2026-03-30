package com.example.SocialMedia.dto.file;

import com.example.SocialMedia.dto.UserSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private Long messageId;
    private List<String> urls;
    private List<String> fileNames;
    private List<Long> fileSizes;
    private String mediaType;
    private UserSummary sender;
    private LocalDateTime sentAt;
}