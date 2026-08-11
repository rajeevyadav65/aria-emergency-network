package com.emergency.system.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource(Environment env) {
        // Prefer explicit Spring datasource URL if provided
        String springUrl = env.getProperty("SPRING_DATASOURCE_URL");
        if (springUrl != null && !springUrl.isEmpty()) {
            return DataSourceBuilder.create().url(springUrl)
                    .username(env.getProperty("SPRING_DATASOURCE_USERNAME"))
                    .password(env.getProperty("SPRING_DATASOURCE_PASSWORD"))
                    .type(HikariDataSource.class)
                    .build();
        }

        // Fall back to DATABASE_URL (Heroku/Railway style)
        String databaseUrl = env.getProperty("DATABASE_URL");
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                URI dbUri = new URI(databaseUrl);
                String[] userInfo = dbUri.getUserInfo() != null ? dbUri.getUserInfo().split(":") : new String[]{};
                String username = userInfo.length > 0 ? userInfo[0] : "";
                String password = userInfo.length > 1 ? userInfo[1] : "";
                String jdbcUrl;
                if (dbUri.getScheme().startsWith("postgres")) {
                    jdbcUrl = String.format("jdbc:postgresql://%s:%d%s", dbUri.getHost(), dbUri.getPort(), dbUri.getPath());
                } else {
                    jdbcUrl = String.format("jdbc:mysql://%s:%d%s", dbUri.getHost(), dbUri.getPort(), dbUri.getPath());
                }
                HikariDataSource ds = DataSourceBuilder.create().type(HikariDataSource.class).build();
                ds.setJdbcUrl(jdbcUrl);
                ds.setUsername(username);
                ds.setPassword(password);
                return ds;
            } catch (Exception e) {
                throw new IllegalStateException("Invalid DATABASE_URL", e);
            }
        }

        // Final fallback: rely on application.properties / SPRING_* env vars
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
