package com.example.SocialMedia.service.social;

import com.example.SocialMedia.dto.response.FollowUserResponse;
import com.example.SocialMedia.dto.response.ShortUserResponse;
import org.springframework.data.domain.Page;

public interface FollowService {
    Boolean getFollow(String followerUsername, String followingUsername);
    ShortUserResponse createFollower(String followerUsername, String followingUsername);
    ShortUserResponse deleteFollower(String followerUsername, String followingUsername);
    Page<FollowUserResponse> getFollowers(String username, int page, int size);
    Page<FollowUserResponse> getFollowing(String username, int page, int size);
    Page<FollowUserResponse> getFriends(String username, int page, int size); // Bổ sung
}
