package com.winwin.dataapi.transform;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class TransformService {

    public String transform(String text) {
        return new StringBuilder(text)
                .reverse()
                .toString()
                .toUpperCase(Locale.ROOT);
    }
}