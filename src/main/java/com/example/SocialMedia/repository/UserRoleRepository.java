package com.example.SocialMedia.repository;

import com.example.SocialMedia.keys.UserRoleId;
import com.example.SocialMedia.model.coredata_model.Role;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.model.coredata_model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    Optional<UserRole> findByRole(Role role);
    Optional<UserRole> findByUserAndRole(User user, Role role);
}
