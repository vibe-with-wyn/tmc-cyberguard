package com.tmc.system.tmc_secure_system.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.tmc.system.tmc_secure_system.entity.enums.FileStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Index;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="encrypted_files", indexes = {
    @Index(name = "idx_encrypted_files_status", columnList = "status"),
    @Index(name = "idx_encrypted_files_uploader", columnList = "uploader_id")
})
public class EncryptedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @ManyToOne
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    @Column(name = "file_hash", nullable = false, length = 128)
    private String fileHash;

    @Column(name = "aes_key_ref", nullable = false, columnDefinition = "TEXT")
    private String aesKeyRef;

    @Column(nullable = false, length = 64)
    private String iv;

    @Lob
    @Column(nullable = false)
    private byte[] ciphertext;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private FileStatus status = FileStatus.ENCRYPTED;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @OneToMany(mappedBy = "file")
    private List<FileAssignment> assignments;
}
