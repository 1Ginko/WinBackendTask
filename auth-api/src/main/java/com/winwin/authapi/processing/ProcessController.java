package com.winwin.authapi.processing;

import com.winwin.authapi.processing.models.requests.ProcessRequest;
import com.winwin.authapi.processing.models.responses.ProcessResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/process")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping
    public ProcessResponse process(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ProcessRequest request
    ) {
        UUID userId = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        String result = processService.process(userId, request.text());

        return new ProcessResponse(result);
    }
}