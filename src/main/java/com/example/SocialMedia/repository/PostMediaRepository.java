package com.example.SocialMedia.repository;

import com.example.SocialMedia.model.coredata_model.Post;
import com.example.SocialMedia.model.coredata_model.PostMedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, Integer> {
    // Hàm này đúng, giữ lại
    Page<PostMedia> findByPost(Post post, Pageable pageable);

    List<PostMedia> findByPost(Post post);
}
