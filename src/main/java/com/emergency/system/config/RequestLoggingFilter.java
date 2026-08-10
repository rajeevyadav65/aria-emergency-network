package com.emergency.system.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Logs every HTTP request with method, path, status code, and duration.
 * Runs after the rate limiter (Order=2) so rate-limited requests are logged too.
 *
 * Output: [GET] /api/emergency/all → 200 (14ms)
 */
@Component
@Order(2)
@Slf4j
public class RequestLoggingFilter implements Filter {

    /** Paths skipped from logging to reduce noise */
    private static final String[] SKIP = {
        "/actuator/", "/h2-console/", "/swagger-ui/", "/v3/api-docs"
    };

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();

        // Skip noisy paths
        for (String skip : SKIP) {
            if (path.startsWith(skip)) {
                chain.doFilter(req, res);
                return;
            }
        }

        long start = System.currentTimeMillis();
        try {
            chain.doFilter(req, res);
        } finally {
            long duration = System.currentTimeMillis() - start;
            int  status   = response.getStatus();

            // Use WARN for errors, DEBUG for normal traffic
            if (status >= 500) {
                log.warn("[{}] {} → {} ({}ms)", request.getMethod(), path, status, duration);
            } else if (status >= 400) {
                log.info("[{}] {} → {} ({}ms)", request.getMethod(), path, status, duration);
            } else {
                log.debug("[{}] {} → {} ({}ms)", request.getMethod(), path, status, duration);
            }
        }
    }
}
