package com.emergency.system.config;

import com.emergency.system.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/dashboard/**",
                                "/dashboard",
                                "/dashboard.html",
                                "/api/maps/**",
                                "/ws/**",
                                "/actuator/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/h2-console/**", // <--- BAS YAHAN ADD KARIYE
                                "/",
                                "/index.html",
                                "/static/**",
                                "/*.js", "/*.css", "/*.png",
                                "/error"
                        ).permitAll()
                        // Baaki ka code waisa hi rehne dein...
                        .requestMatchers(HttpMethod.POST,
                                "/api/emergency/report",
                                "/api/emergency/ambulance/book",
                                "/api/ai/**",
                                "/api/chat/message",
                                "/api/location/update",
                                "/api/location/share",
                                "/api/location-tracking/share",
                                "/api/user-location/update",
                                "/api/sync/batch").permitAll()
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/chat/clear",
                                "/api/location/share/**",
                                "/api/location-tracking/share/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/chat/history",
                                "/api/emergency/search",
                                "/api/disasters/active",
                                "/api/disasters/nearby",
                                "/api/analytics/**",
                                "/api/medical/doctors/nearby",
                                "/api/medical/ambulances/nearby",
                                "/api/medical/police/nearby",
                                "/api/dispatch/doctors",
                                "/api/dispatch/police",
                                "/api/dispatch/ambulances",
                                "/api/location/history",
                                "/api/location/history/**",
                                "/api/location/share/active",
                                "/api/location-tracking/history/**",
                                "/api/location-tracking/share/active",
                                "/api/sync/pending").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Component
    @Slf4j
    @RequiredArgsConstructor
    public static class JwtAuthFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;
        @Lazy
        private final UserService userService;

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain chain) throws ServletException, IOException {

            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
                    if (jwtUtil.isTokenValid(token)) {
                        String subject = jwtUtil.extractSubject(token);
                        if (subject != null && !subject.startsWith("guest:")) {
                            UserDetails ud = userService.loadUserByUsername(subject);
                            var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                } catch (Exception e) {
                    log.debug("Auth setup failed: {}", e.getMessage());
                }
            }
            chain.doFilter(request, response);
        }
    }
}
