package com.winwin.authapi.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = ProcessingLogEntity.PROCESSING_LOG_TABLE)
public class ProcessingLogEntity {

    static final String PROCESSING_LOG_TABLE = "processing_log";
    static final String LOG_ID = "id";
    static final String LOG_USER_ID = "user_id";
    static final String LOG_INPUT_TEXT = "input_text";
    static final String LOG_OUTPUT_TEXT = "output_text";
    static final String LOG_CREATED_AT = "created_at";

    @Id
    @Column(name = LOG_ID, nullable = false, updatable = false)
    private UUID id;

    @Column(name = LOG_USER_ID, nullable = false, updatable = false)
    private UUID userId;

    @Column(name = LOG_INPUT_TEXT, nullable = false, columnDefinition = "TEXT")
    private String inputText;

    @Column(name = LOG_OUTPUT_TEXT, nullable = false, columnDefinition = "TEXT")
    private String outputText;

    @Column(name = LOG_CREATED_AT, nullable = false, updatable = false)
    private Instant createdAt;

    protected ProcessingLogEntity() {}

    public ProcessingLogEntity(
            UUID id,
            UUID userId,
            String inputText,
            String outputText,
            Instant createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.inputText = inputText;
        this.outputText = outputText;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getInputText() {
        return inputText;
    }

    public String getOutputText() {
        return outputText;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}