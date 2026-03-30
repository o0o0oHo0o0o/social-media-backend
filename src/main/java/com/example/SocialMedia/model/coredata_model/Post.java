package com.example.SocialMedia.model.coredata_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Posts", schema = "CoreData")
@Setter
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PostID", unique = true, nullable = false)
    private int postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InteractableItemID", unique = true, nullable = false)
    private InteractableItems interactableItem;

    @Column(name = "Content")
    private String content;

    @Column(name = "PostTopic", nullable = false)
    private String postTopic;

    @Column(name = "Location")
    private String location;

    @Column(name = "IsArchived")
    private boolean isArchived = false;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdLocalDateTime;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedLocalDateTime;

    @Column(name = "isDeleted")
    private boolean isDeleted = false;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedLocalDateTime;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostMedia> postMedias;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostTag> postTags;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "TargetInteractableItemID", referencedColumnName = "InteractableItemID")
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Share> shares;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "InteractableItemID", referencedColumnName = "InteractableItemID")
    private Set<Reaction> reactions;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Report> reports;
}
