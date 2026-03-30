package com.example.SocialMedia.service.message;

import com.example.SocialMedia.dto.request.MarkReadRequest;

public interface MessageStatusService {
    void markAsRead(String username, MarkReadRequest request);
}
