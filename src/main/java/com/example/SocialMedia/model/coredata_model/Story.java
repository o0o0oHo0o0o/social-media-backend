package com.example.SocialMedia.model.coredata_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Stories", schema = "CoreData")
@Setter
@Getter
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StoryID")
    private int StoryID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InteractableItemID")
    private InteractableItems interactableItem;

    @Column(name = "MediaURL")
    private String MediaURL;

    @Column(name = "MediaType")
    private String MediaType;

    @Column(name = "CreatedAt")
    private Date CreateAt;

    @Column(name = "ExpiresAt")
    private Date ExpiresAt;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "InteractableItemID", referencedColumnName = "InteractableItemID")
    private List<Reaction> reactions;

}
