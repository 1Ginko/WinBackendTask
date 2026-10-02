package com.winwin.authapi.data.repository;

import com.winwin.authapi.data.entity.ProcessingLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessingLogRepository extends JpaRepository<ProcessingLogEntity, UUID> { }