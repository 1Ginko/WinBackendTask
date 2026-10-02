package com.winwin.authapi.processing;

import com.winwin.authapi.data.entity.ProcessingLogEntity;
import com.winwin.authapi.data.repository.ProcessingLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ProcessService {

    private final DataApiClient dataApiClient;
    private final ProcessingLogRepository processingLogRepository;

    public ProcessService(DataApiClient dataApiClient, ProcessingLogRepository processingLogRepository) {
        this.dataApiClient = dataApiClient;
        this.processingLogRepository = processingLogRepository;
    }

    public String process(UUID userId, String inputText) {
        String outputText = dataApiClient.transform(inputText);
        ProcessingLogEntity userLog = new ProcessingLogEntity(
                UUID.randomUUID(),
                userId,
                inputText,
                outputText,
                Instant.now()
        );
        processingLogRepository.save(userLog);

        return outputText;
    }
}