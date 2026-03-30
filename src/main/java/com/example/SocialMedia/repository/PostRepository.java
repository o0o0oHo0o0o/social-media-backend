package com.example.SocialMedia.repository;

import com.example.SocialMedia.model.coredata_model.Post;
import com.example.SocialMedia.model.coredata_model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    // Find a post by its ID
    Optional<Post> findByPostId(long postId);

    Page<Post> findByUserAndIsDeletedIsFalse(User user, Pageable pageable);

    Optional<Post> findByInteractableItem_InteractableItemId(int interactableItemId);

    @Query(value = "select  * from CoreData.Posts WHERE Content LIKE %:content% COLLATE Latin1_General_CI_AI", nativeQuery = true)
    Page<Post> findByContentContainingIgnoreCase(@Param("content") String content, Pageable pageable);
}
