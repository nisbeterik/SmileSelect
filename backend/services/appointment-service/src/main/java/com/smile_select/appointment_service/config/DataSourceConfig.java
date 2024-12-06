package com.smile_select.appointment_service.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String primaryUrl;

    @Value("${spring.datasource.username}")
    private String primaryUsername;

    @Value("${spring.datasource.password}")
    private String primaryPassword;

    @Value("${fallback.datasource.url}")
    private String fallbackUrl;

    @Value("${fallback.datasource.username}")
    private String fallbackUsername;

    @Value("${fallback.datasource.password}")
    private String fallbackPassword;

    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create()
                .url(primaryUrl)
                .username(primaryUsername)
                .password(primaryPassword)
                .build();
    }

    @Bean(name = "fallbackDataSource")
    public DataSource fallbackDataSource() {
        return DataSourceBuilder.create()
                .url(fallbackUrl)
                .username(fallbackUsername)
                .password(fallbackPassword)
                .build();
    }

    @Bean(name = "primaryJdbcTemplate")
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "fallbackJdbcTemplate")
    public JdbcTemplate fallbackJdbcTemplate(@Qualifier("fallbackDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
