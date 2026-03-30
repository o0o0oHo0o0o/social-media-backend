package com.example.SocialMedia.service.message;

import com.example.SocialMedia.dto.message.PresenceState;

public interface PresenceService {
    void touch(Integer userId, String username);
    void setOffline(Integer userId, String username);
    PresenceState getState(Integer userId, String username);
    boolean isOnlineWithin(Integer userId, String username, long withinSeconds);
}