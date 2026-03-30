package com.example.SocialMedia.model.coredata_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Shares", schema = "CoreData")
@Setter
@Getter
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ShareID")
    private int shareId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OriginalPostID", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InteractableItemID")
    private InteractableItems interactableItem;

    @Column(name = "ShareCaption")
    private String shareCaption;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime sharedLocalDateTime;
}
