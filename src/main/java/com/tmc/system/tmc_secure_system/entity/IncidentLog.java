package com.tmc.system.tmc_secure_system.entity;

import java.time.LocalDateTime;

import com.tmc.system.tmc_secure_system.entity.enums.IncidentSeverity;
import com.tmc.system.tmc_secure_system.entity.enums.IncidentStatus;
import com.tmc.system.tmc_secure_system.entity.enums.IncidentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "incident_logs")
public class IncidentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_time")
    private LocalDateTime eventTime = LocalDateTime.now();

    @Column(length = 100)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 64)
    private IncidentType eventType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private IncidentSeverity severity = IncidentSeverity.LOW;

    @Enumerated(EnumType.STRING)
    @Column(length = 16, nullable = false)
    private IncidentStatus status = IncidentStatus.OPEN;
    
    @ManyToOne
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "session_id", length = 64)
    private String sessionId;
}
