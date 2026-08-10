package com.emergency.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * CORS: allow configured origins (default: * for dev, restrict in prod).
     * In production set: app.cors.allowed-origins=https://your-frontend.com
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins.equals("*") ? "*" : allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-Record-Count", "Content-Disposition")
                .allowCredentials(!allowedOrigins.equals("*"))
                .maxAge(3600);
    }
}
