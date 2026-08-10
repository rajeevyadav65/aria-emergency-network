package com.emergency.system.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the IP-based rate limiter filter.
 * No Spring context needed — pure unit tests with mock objects.
 */
class RateLimitFilterTest {

    private RateLimitFilter filter;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter();
        ReflectionTestUtils.setField(filter, "enabled", true);
        ReflectionTestUtils.setField(filter, "requestsPerMinute", 3); // low limit for testing
        chain = mock(FilterChain.class);
    }

    @Test
    @DisplayName("First request on rate-limited path passes through")
    void firstRequest_passesThrough() throws Exception {
        MockHttpServletRequest req = rateRequest("/api/auth/login", "10.0.0.1");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        filter.doFilter(req, resp, chain);

        verify(chain, times(1)).doFilter(req, resp);
        assertThat(resp.getStatus()).isNotEqualTo(429);
    }

    @Test
    @DisplayName("Requests within limit all pass through")
    void withinLimit_allPass() throws Exception {
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest req = rateRequest("/api/auth/login", "10.0.0.2");
            MockHttpServletResponse resp = new MockHttpServletResponse();
            filter.doFilter(req, resp, chain);
            assertThat(resp.getStatus()).isNotEqualTo(429);
        }
        verify(chain, times(3)).doFilter(any(), any());
    }

    @Test
    @DisplayName("Exceeding limit returns 429")
    void exceedingLimit_returns429() throws Exception {
        String ip = "10.0.0.3";
        // Send 3 allowed requests
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest req = rateRequest("/api/auth/login", ip);
            MockHttpServletResponse resp = new MockHttpServletResponse();
            filter.doFilter(req, resp, chain);
        }
        // 4th should be blocked
        MockHttpServletRequest req4 = rateRequest("/api/auth/login", ip);
        StringWriter sw = new StringWriter();
        MockHttpServletResponse resp4 = new MockHttpServletResponse();
        filter.doFilter(req4, resp4, chain);
        assertThat(resp4.getStatus()).isEqualTo(429);
    }

    @Test
    @DisplayName("Non-rate-limited path always passes through")
    void nonRateLimitedPath_alwaysPasses() throws Exception {
        for (int i = 0; i < 100; i++) {
            MockHttpServletRequest req = rateRequest("/api/emergency/active", "10.0.0.4");
            MockHttpServletResponse resp = new MockHttpServletResponse();
            filter.doFilter(req, resp, chain);
            assertThat(resp.getStatus()).isNotEqualTo(429);
        }
    }

    @Test
    @DisplayName("Different IPs have independent rate limits")
    void differentIps_independentLimits() throws Exception {
        // Fill up IP A's limit
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest req = rateRequest("/api/auth/login", "192.168.1.1");
            filter.doFilter(req, new MockHttpServletResponse(), chain);
        }
        // IP B should still pass
        MockHttpServletRequest reqB = rateRequest("/api/auth/login", "192.168.1.2");
        MockHttpServletResponse respB = new MockHttpServletResponse();
        filter.doFilter(reqB, respB, chain);
        assertThat(respB.getStatus()).isNotEqualTo(429);
    }

    @Test
    @DisplayName("Filter disabled — all requests pass regardless of rate")
    void disabled_allRequestsPass() throws Exception {
        ReflectionTestUtils.setField(filter, "enabled", false);
        for (int i = 0; i < 100; i++) {
            MockHttpServletRequest req = rateRequest("/api/auth/login", "10.0.0.5");
            MockHttpServletResponse resp = new MockHttpServletResponse();
            filter.doFilter(req, resp, chain);
            assertThat(resp.getStatus()).isNotEqualTo(429);
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private MockHttpServletRequest rateRequest(String path, String ip) {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", path);
        req.setRemoteAddr(ip);
        return req;
    }
}
