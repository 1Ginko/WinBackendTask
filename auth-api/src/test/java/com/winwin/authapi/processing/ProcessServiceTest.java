package com.winwin.authapi.processing;

import com.winwin.authapi.data.entity.ProcessingLogEntity;
import com.winwin.authapi.data.repository.ProcessingLogRepository;
import com.winwin.authapi.exceptions.DataApiUnavailableException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class ProcessServiceTest {

    private final DataApiClient dataApiClient = mock(DataApiClient.class);
    private final ProcessingLogRepository processingLogRepository =
            mock(ProcessingLogRepository.class);
    private final ProcessService processService = new ProcessService(dataApiClient, processingLogRepository);

    @Test
    void transformsTextAndSavesProcessingLog() {
        UUID userId = UUID.randomUUID();
        given(dataApiClient.transform("hello")).willReturn("OLLEH");
        Instant beforeProcessing = Instant.now();

        String result = processService.process(userId, "hello");

        Instant afterProcessing = Instant.now();
        ArgumentCaptor<ProcessingLogEntity> logCaptor = ArgumentCaptor.forClass(ProcessingLogEntity.class);
        then(processingLogRepository).should().save(logCaptor.capture());

        ProcessingLogEntity savedLog = logCaptor.getValue();
        assertThat(result).isEqualTo("OLLEH");
        assertThat(savedLog.getId()).isNotNull();
        assertThat(savedLog.getUserId()).isEqualTo(userId);
        assertThat(savedLog.getInputText()).isEqualTo("hello");
        assertThat(savedLog.getOutputText()).isEqualTo("OLLEH");
        assertThat(savedLog.getCreatedAt()).isBetween(beforeProcessing, afterProcessing);
    }

    @Test
    void doesNotSaveProcessingLogWhenDataApiFails() {
        UUID userId = UUID.randomUUID();
        given(dataApiClient.transform("hello"))
                .willThrow(new DataApiUnavailableException());

        assertThatThrownBy(() -> processService.process(userId, "hello"))
                .isInstanceOf(DataApiUnavailableException.class);

        then(processingLogRepository).shouldHaveNoInteractions();
    }
}
