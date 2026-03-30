package com.example.SocialMedia.keys;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserRoleId implements Serializable {
    private int userId;
    private int roleId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserRoleId other = (UserRoleId) obj;
        if (userId != other.userId) return false;
        return roleId == other.roleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.roleId);
    }
}
