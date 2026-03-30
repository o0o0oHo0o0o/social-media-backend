package com.example.SocialMedia.repository;

import com.example.SocialMedia.keys.FollowId;
import com.example.SocialMedia.model.coredata_model.Follow;
import com.example.SocialMedia.model.coredata_model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    Optional<Follow> findByUserFollowerAndUserFollowing(User userFollower, User userFollowing);

    // Đã đổi tên hàm cho khớp với Entity (userFollower và userFollowing)
    boolean existsByUserFollower_IdAndUserFollowing_Id(Integer followerId, Integer followingId);

    // Lấy danh sách Bạn bè (Mutual Follow - Trả về nguyên object User có phân trang)
    @Query("""
        SELECT f1.userFollowing 
        FROM Follow f1 
        WHERE f1.userFollower.id = :userId 
        AND EXISTS (
            SELECT 1 
            FROM Follow f2 
            WHERE f2.userFollower.id = f1.userFollowing.id 
            AND f2.userFollowing.id = :userId
        )
    """)
    Page<User> findFriends(@Param("userId") Integer userId, Pageable pageable);

    // Lấy danh sách ID bạn bè (Mutual Follow)
    @Query("""
        SELECT f1.userFollowing.id 
        FROM Follow f1 
        WHERE f1.userFollower.id = :meId 
        AND EXISTS (
            SELECT 1 
            FROM Follow f2 
            WHERE f2.userFollower.id = f1.userFollowing.id 
            AND f2.userFollowing.id = :meId
        )
    """)
    List<Integer> findMutualFriendIds(@Param("meId") Integer meId);

    // Lấy danh sách những người đang theo dõi user này
    @Query("SELECT f.userFollower FROM Follow f WHERE f.userFollowing.id = :userId")
    Page<User> findFollowers(@Param("userId") Integer userId, Pageable pageable);

    // Lấy danh sách những người user này đang theo dõi
    @Query("SELECT f.userFollowing FROM Follow f WHERE f.userFollower.id = :userId")
    Page<User> findFollowing(@Param("userId") Integer userId, Pageable pageable);
}