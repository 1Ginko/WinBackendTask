package com.winwin.dataapi.transform;

import com.winwin.dataapi.transform.requests.TransformRequest;
import com.winwin.dataapi.transform.responses.TransformResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transform")
public class TransformController {

    private final TransformService transformService;

    public TransformController(TransformService transformService) {
        this.transformService = transformService;
    }

    @PostMapping
    public TransformResponse transform(@Valid @RequestBody TransformRequest request) {
        String result = transformService.transform(request.text());
        return new TransformResponse(result);
    }
}