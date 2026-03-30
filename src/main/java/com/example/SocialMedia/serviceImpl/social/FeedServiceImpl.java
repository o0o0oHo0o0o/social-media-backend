package com.example.SocialMedia.serviceImpl.social;

import com.example.SocialMedia.dto.response.PostResponse;
import com.example.SocialMedia.exception.ResourceNotFound.UserNotFoundException;
import com.example.SocialMedia.mapper.PostMapper;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.repository.*;
import com.example.SocialMedia.service.social.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    private final PostMapper postMapper;

    // --- MAIN METHODS ---

    @Override
    public List<PostResponse> getHome(String username, Pageable pageable) {
        // Lấy ID từ username
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Gọi Repository
        return feedRepository.findFeed(user.getId(), pageable)
                .getContent().stream()
                .map(postMapper::toPostResponse) // <--- Gọi Mapper cực gọn
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getPopular(Pageable pageable) {
        return feedRepository.findPopular(pageable)
                .getContent().stream()
                .map(postMapper::toPostResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getDiscussion(Pageable pageable) {
        return feedRepository.findDiscussion(pageable)
                .getContent()
                .stream()
                .map(postMapper::toPostResponse)
                .collect(Collectors.toList());
    }
}