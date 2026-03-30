package com.example.SocialMedia.model.coredata_model;

import com.example.SocialMedia.keys.BlockId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
@Entity
@Table(name = "Blocks", schema = "CoreData")
@Setter
@Getter
public class Block {
    @EmbeddedId
    private BlockId blockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("blockerId")
    @JoinColumn(name = "BlockerID", nullable = false)
    private User blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("blockedUserId")
    @JoinColumn(name = "BlockedUserID", nullable = false)
    private User blockedUser;

    @Column(name = "BlockedAt", nullable = false)
    private LocalDateTime blockedLocalDateTime;
}
