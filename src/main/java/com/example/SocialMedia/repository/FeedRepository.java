package com.example.SocialMedia.repository;

import com.example.SocialMedia.model.coredata_model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p " +
            "JOIN Follow f ON f.userFollower.id = p.user.id " +
            "WHERE f.userFollowing.id = :userId " +
            "AND p.isDeleted = false " +
            "ORDER BY p.createdLocalDateTime DESC")
    Page<Post> findFeed(@Param("userId") Integer userId, Pageable pageable);
    @Query("SELECT p FROM Post p " +
            "LEFT JOIN Comment c ON c.post.postId = p.postId AND c.isDeleted = false " +
            "LEFT JOIN Reaction r ON r.interactableItems.interactableItemId = p.interactableItem.interactableItemId " +
            "WHERE p.isDeleted = false " +
            "GROUP BY p " +
            "ORDER BY COUNT(c) + COUNT(r) DESC, p.createdLocalDateTime DESC")
    Page<Post> findPopular(Pageable pageable);
    @Query("SELECT p FROM Post p " +
            "WHERE p.isDeleted = false " +
            "AND p.postTopic = 'Discussion' ")
    Page<Post> findDiscussion(Pageable pageable);
}
