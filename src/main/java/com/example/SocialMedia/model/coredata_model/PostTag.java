package com.example.SocialMedia.model.coredata_model;

import com.example.SocialMedia.keys.PostTagId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PostTags", schema = "CoreData")
@Setter
@Getter
public class PostTag {
    @EmbeddedId
    private PostTagId postTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "PostID", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taggedUserId")
    @JoinColumn(name = "TaggedUserID", nullable = false)
    private User taggedUser;

    @Column(name = "TaggedAt", nullable = false)
    private LocalDateTime taggedLocalDateTime;
}
