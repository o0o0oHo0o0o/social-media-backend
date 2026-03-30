package com.example.SocialMedia.dto.response;

import com.example.SocialMedia.constant.ReactionType;

public interface ReactionStat {
    ReactionType getReactionType();
    Long getReactionCount();
}