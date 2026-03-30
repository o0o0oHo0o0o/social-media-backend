package com.example.SocialMedia.service.social;

import com.example.SocialMedia.dto.response.PostResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface FeedService {
    List<PostResponse> getHome(String username, Pageable pageable); // Sửa int -> String
    List<PostResponse> getPopular(Pageable pageable);
    List<PostResponse> getDiscussion(Pageable pageable);
}