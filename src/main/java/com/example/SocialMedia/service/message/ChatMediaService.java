package com.example.SocialMedia.service.message;

import com.example.SocialMedia.dto.MemberDto;
import com.example.SocialMedia.dto.file.FileDto;
import com.example.SocialMedia.dto.file.PhotoDto;
import org.springframework.data.domain.Page;
import java.util.List;

public interface ChatMediaService {
    public Page<PhotoDto> getPhotos(int conversationId, int page, int size);
    public Page<FileDto> getFiles(int conversationId, int page, int size);
    public List<MemberDto> getMembers(int conversationId);
}
