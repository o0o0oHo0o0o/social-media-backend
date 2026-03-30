package com.example.SocialMedia.model.coredata_model;

import com.example.SocialMedia.constant.TargetType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "InteractableItems", schema = "CoreData")
@Setter
@Getter
public class InteractableItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InteractableItemID")
    private int interactableItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ItemType")
    private TargetType itemType;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "interactableItems", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reaction> reactions;

}
