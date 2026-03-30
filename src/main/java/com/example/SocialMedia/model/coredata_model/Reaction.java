package com.example.SocialMedia.model.coredata_model;

import com.example.SocialMedia.constant.ReactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reactions", schema = "CoreData")
@Setter
@Getter
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReactionID")
    private int reactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InteractableItemID", nullable = false)
    private InteractableItems interactableItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "ReactionType", nullable = false)
    private ReactionType reactionType;

    @Column(name = "ReactedAt", nullable = false)
    private LocalDateTime reactedLocalDateTime;
}
