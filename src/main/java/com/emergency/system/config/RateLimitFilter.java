package com.emergency.system.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple sliding-window rate limiter per IP address.
 * Protects the emergency report and auth endpoints from abuse.
 *
 * Default: 30 requests per minute per IP on protected paths.
 * Completely missing from original project.
 */
@Component
@Order(1)
@Slf4j
public class RateLimitFilter implements Filter {

    @Value("${app.rate-limit.requests-per-minute:30}")
    private int requestsPerMinute;

    @Value("${app.rate-limit.enabled:true}")
    private boolean enabled;

    // IP → [count, windowStartMillis]
    private final Map<String, long[]> requestCounts = new ConcurrentHashMap<>();

    private static final String[] RATE_LIMITED_PATHS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/emergency/report",
            "/api/chat/message"
    };

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        if (!enabled) { chain.doFilter(req, res); return; }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();
        boolean isRateLimited = false;
        for (String p : RATE_LIMITED_PATHS) {
            if (path.startsWith(p)) { isRateLimited = true; break; }
        }

        if (isRateLimited) {
            String ip = getClientIp(request);
            if (!allowRequest(ip)) {
                log.warn("Rate limit exceeded for IP: {} on path: {}", ip, path);
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"error\":\"Too many requests — please wait before trying again.\",\"status\":429}");
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private boolean allowRequest(String ip) {
        long now = Instant.now().toEpochMilli();
        long windowMs = 60_000L;

        requestCounts.compute(ip, (key, val) -> {
            if (val == null || now - val[1] > windowMs) {
                return new long[]{1, now};
            }
            val[0]++;
            return val;
        });

        long[] entry = requestCounts.get(ip);
        return entry[0] <= requestsPerMinute;
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) return realIp;
        return request.getRemoteAddr();
    }
}
