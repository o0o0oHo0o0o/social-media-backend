package com.example.SocialMedia.model.coredata_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.time.LocalDateTime;


@Entity
@Table(name = "Comments", schema = "CoreData")
@Setter
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentID")
    private int commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OwnInteractableItemID")
    private InteractableItems ownInteractableItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ParentCommentID")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY)
    private List<Comment> replies;

    @Column(name = "Content", nullable = false)
    private String content;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdLocalDateTime;

    @Column(name = "IsDeleted")
    private boolean isDeleted;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TargetInteractableItemID", referencedColumnName = "InteractableItemID")
    private Post post;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "InteractableItemID", referencedColumnName = "OwnInteractableItemID")
    private List<Reaction> reactions;

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY)
    private List<Report> reports;
}
