package com.example.SocialMedia.model.coredata_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "PostMedia", schema = "CoreData")
@Setter
@Getter
public class PostMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MediaID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PostID")
    private Post post;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InteractableItemID")
    private InteractableItems interactableItem;

    @Column(name = "FileName", nullable = false)
    private String fileName;

    @Column(name = "MediaType", nullable = false)
    private String mediaType; // IMAGE, VIDEO

    @Column(name = "SortOrder")
    private int sortOrder;
}