package com.tmc.system.tmc_secure_system.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.tmc.system.tmc_secure_system.entity.enums.RoleName;
import com.tmc.system.tmc_secure_system.entity.enums.UserStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_status", columnList = "status"),
    @Index(name = "idx_users_email", columnList = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private RoleName role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "failed_attempts")
    private int failedAttempts;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Relationships
    @OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL)
    private List<EncryptedFile> uploadedFiles;

    @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL)
    private List<IncidentLog> logs;

    // Convenience back-links
    @OneToMany(mappedBy = "analyst", cascade = CascadeType.ALL)
    private List<FileAssignment> assignmentsAsAnalyst;

    @OneToMany(mappedBy = "assignedBy", cascade = CascadeType.ALL)
    private List<FileAssignment> assignmentsMade;

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (email != null) email = email.trim().toLowerCase();
        if (username != null) username = username.trim();
        updatedAt = LocalDateTime.now();
    }
}
