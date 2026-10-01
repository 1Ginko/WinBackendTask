package com.winwin.dataapi.transform.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InternalTokenInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "X-Internal-Token";

    private final String expectedToken;

    public InternalTokenInterceptor(@Value("${app.internal-token}") String expectedToken) {
        this.expectedToken = expectedToken;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {
        String actualToken = request.getHeader(TOKEN_HEADER);

        if (!expectedToken.equals(actualToken)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid internal token");
            return false;
        }

        return true;
    }
}