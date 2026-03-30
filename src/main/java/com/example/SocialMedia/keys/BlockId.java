package com.example.SocialMedia.keys;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BlockId implements Serializable {
    private int blockerId;
    private int blockedUserId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockId blockId = (BlockId) o;
        if (blockerId != blockId.blockerId) return false;
        return blockedUserId == blockId.blockedUserId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockerId, blockedUserId);
    }
}
