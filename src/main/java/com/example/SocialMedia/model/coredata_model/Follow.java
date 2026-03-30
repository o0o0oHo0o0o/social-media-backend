package com.example.SocialMedia.model.coredata_model;

import com.example.SocialMedia.keys.FollowId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Follows", schema = "CoreData")
@Setter
@Getter
public class Follow {
    @EmbeddedId
    private FollowId followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")
    @JoinColumn(name = "FollowerID", nullable = false)
    private User userFollower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followingId")
    @JoinColumn(name = "FollowingID", nullable = false)
    private User userFollowing;

    @Column(name = "FollowedAt",  nullable = false)
    private LocalDateTime followedLocalDateTime;

}
