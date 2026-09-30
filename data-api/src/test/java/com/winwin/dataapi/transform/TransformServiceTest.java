package com.winwin.dataapi.transform;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransformServiceTest {

    private final TransformService transformService = new TransformService();

    @Test
    void reversesAndUppercasesText() {
        String result = transformService.transform("hello");

        assertThat(result).isEqualTo("OLLEH");
    }
}
