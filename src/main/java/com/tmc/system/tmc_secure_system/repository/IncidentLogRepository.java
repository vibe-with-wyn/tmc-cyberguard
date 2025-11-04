package com.tmc.system.tmc_secure_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tmc.system.tmc_secure_system.entity.IncidentLog;

@Repository
public interface IncidentLogRepository extends JpaRepository<IncidentLog, Long> {
}
