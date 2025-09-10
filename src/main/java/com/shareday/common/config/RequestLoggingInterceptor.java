package com.shareday.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class RequestLoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        // 아주 가벼운 로그 — 필요시 MDC/traceId 등 확장
        String method = req.getMethod();
        String uri = req.getRequestURI();
        String query = req.getQueryString();
        System.out.printf("[REQ] %s %s%s%n",
                method, uri, (query != null ? "?" + query : ""));
        return true;
    }
}