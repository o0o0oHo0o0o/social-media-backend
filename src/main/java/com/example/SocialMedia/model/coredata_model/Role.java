package com.example.SocialMedia.model.coredata_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "Roles", schema = "CoreData")
@Setter
@Getter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleID")
    private int roleID;

    @Column(name = "RoleName", nullable = false)
    private String roleName;

    @OneToMany(mappedBy = "role", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserRole> userRoles;
}
