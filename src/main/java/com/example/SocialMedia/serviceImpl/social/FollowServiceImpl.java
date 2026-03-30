package com.example.SocialMedia.serviceImpl.social;

import com.example.SocialMedia.dto.response.FollowUserResponse;
import com.example.SocialMedia.dto.response.ShortUserResponse;
import com.example.SocialMedia.exception.BusinessException;
import com.example.SocialMedia.exception.ResourceNotFound.ResourceNotFoundException;
import com.example.SocialMedia.exception.ResourceNotFound.UserNotFoundException;
import com.example.SocialMedia.keys.FollowId;
import com.example.SocialMedia.model.coredata_model.Follow;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.repository.FollowRepository;
import com.example.SocialMedia.repository.UserRepository;
import com.example.SocialMedia.service.IMinioService;
import com.example.SocialMedia.service.social.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    private final IMinioService minioService;
    @Override
    public Boolean getFollow(String followerUsername, String followingUsername) {
        User userFollower = userRepository.findByUserName(followerUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + followerUsername));
        User userFollowing = userRepository.findByUserName(followingUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + followingUsername));
        return followRepository.findByUserFollowerAndUserFollowing(userFollower, userFollowing).isPresent();
    }
    @Override
    public ShortUserResponse createFollower(String followerUsername, String followingUsername) {
        User userFollower = userRepository.findByUserName(followerUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + followerUsername));
        User userFollowing = userRepository.findByUserName(followingUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + followingUsername));
        Follow follow = new Follow();
        FollowId followId = new FollowId();
        followId.setFollowerId(userFollower.getId());
        followId.setFollowingId(userFollowing.getId());

        follow.setFollowId(followId);  // Set the composite key
        follow.setUserFollower(userFollower);
        follow.setUserFollowing(userFollowing);
        follow.setFollowedLocalDateTime(LocalDateTime.now());
        followRepository.save(follow);
        return new ShortUserResponse(
                userFollower.getId(),
                userFollower.getFullName(),
                userFollower.getUsername(),
                userFollower.getProfilePictureURL(),
                userFollower.getCreatedLocalDateTime());
    }
    @Override
    public ShortUserResponse deleteFollower(String followerUsername, String followingUsername) {
        User userFollower = userRepository.findByUserName(followerUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + followerUsername));
        User userFollowing = userRepository.findByUserName(followingUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + followingUsername));
        Follow follow = followRepository.findByUserFollowerAndUserFollowing(userFollower, userFollowing)
                .orElseThrow(() -> new ResourceNotFoundException("Follow not found: " + followerUsername));
        followRepository.delete(follow);
        return new ShortUserResponse(
                userFollower.getId(),
                userFollower.getFullName(),
                userFollower.getUsername(),
                userFollower.getProfilePictureURL(),
                userFollower.getCreatedLocalDateTime());
    }

    @Override
    public Page<FollowUserResponse> getFollowers(String username, int page, int size) {
        // 1. Tìm user đang được xem profile
        User targetUser = userRepository.findByUserName(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Không tìm thấy người dùng: " + username));

        // 2. Xử lý page (Frontend đang gọi page=0 nên không cần trừ đi 1)
        int dbPage = Math.max(0, page);

        // 3. Query DB
        Page<User> followers = followRepository.findFollowers(targetUser.getId(), PageRequest.of(dbPage, size));

        // 4. Map sang DTO
        return followers.map(follower -> mapToFollowResponse(targetUser.getId(), follower));
    }

    @Override
    public Page<FollowUserResponse> getFollowing(String username, int page, int size) {
        User targetUser = userRepository.findByUserName(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Không tìm thấy người dùng: " + username));

        int dbPage = Math.max(0, page);

        Page<User> following = followRepository.findFollowing(targetUser.getId(), PageRequest.of(dbPage, size));

        return following.map(followedUser -> mapToFollowResponse(targetUser.getId(), followedUser));
    }

    @Override
    public Page<FollowUserResponse> getFriends(String username, int page, int size) {
        User targetUser = userRepository.findByUserName(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Không tìm thấy người dùng: " + username));

        int dbPage = Math.max(0, page);

        Page<User> friends = followRepository.findFriends(targetUser.getId(), PageRequest.of(dbPage, size));

        return friends.map(friend -> mapToFollowResponse(targetUser.getId(), friend));
    }

    /**
     * Hàm helper dùng chung để map từ Entity User sang FollowUserResponse
     */
    private FollowUserResponse mapToFollowResponse(Integer targetUserId, User listUser) {
        // 1. Xử lý link Avatar qua MinIO
        String avatar = listUser.getProfilePictureURL();
        if (avatar != null && !avatar.isBlank() && !avatar.startsWith("http")) {
            avatar = minioService.getFileUrl(avatar);
        }

        // 2. Kiểm tra xem 2 người có follow chéo nhau không (Mutual Follow)
        boolean mutual = followRepository.existsByUserFollower_IdAndUserFollowing_Id(targetUserId, listUser.getId()) &&
                followRepository.existsByUserFollower_IdAndUserFollowing_Id(listUser.getId(), targetUserId);

        // 3. Đóng gói Response
        return FollowUserResponse.builder()
                .userId(listUser.getId())
                .username(listUser.getUsername())
                .fullName(listUser.getFullName())
                .avatarUrl(avatar)
                .mutualFollow(mutual)
                .build();
    }
}
