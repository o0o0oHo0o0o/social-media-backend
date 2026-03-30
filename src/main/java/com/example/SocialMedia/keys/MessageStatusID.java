package com.example.SocialMedia.keys;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Embeddable
@Getter
@Setter
public class MessageStatusID {
    private long messageID;
    private int userID;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof MessageStatusID other)) return false;
        return other.messageID == messageID && other.userID == userID;
    }
    @Override
    public int hashCode() {
        return Objects.hash(userID, messageID);
    }
}
