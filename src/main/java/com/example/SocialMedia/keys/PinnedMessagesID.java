package com.example.SocialMedia.keys;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PinnedMessagesID implements Serializable {
    private int conversationID;
    private long messageID;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof PinnedMessagesID other)) return false;
        return other.messageID == messageID && other.conversationID == conversationID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationID, messageID);
    }
}
