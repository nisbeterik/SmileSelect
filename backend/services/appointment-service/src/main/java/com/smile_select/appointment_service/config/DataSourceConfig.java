package com.smile_select.appointment_service.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;

@Configuration
public class DataSourceConfig {

    @Value("${counter.datasource.url}")
    private String counterUrl;
    @Value("${counter.datasource.username}")
    private String counterUsername;
    @Value("${counter.datasource.password}")
    private String counterPassword;

    @Value("${partition0.datasource.url}")
    private String p0PrimaryUrl;
    @Value("${partition0.datasource.username}")
    private String p0PrimaryUsername;
    @Value("${partition0.datasource.password}")
    private String p0PrimaryPassword;

    @Value("${partition0.fallback.datasource.url}")
    private String p0FallbackUrl;
    @Value("${partition0.fallback.datasource.username}")
    private String p0FallbackUsername;
    @Value("${partition0.fallback.datasource.password}")
    private String p0FallbackPassword;

    @Value("${partition1.datasource.url}")
    private String p1PrimaryUrl;
    @Value("${partition1.datasource.username}")
    private String p1PrimaryUsername;
    @Value("${partition1.datasource.password}")
    private String p1PrimaryPassword;

    @Value("${partition1.fallback.datasource.url}")
    private String p1FallbackUrl;
    @Value("${partition1.fallback.datasource.username}")
    private String p1FallbackUsername;
    @Value("${partition1.fallback.datasource.password}")
    private String p1FallbackPassword;

    @Value("${partition2.datasource.url}")
    private String p2PrimaryUrl;
    @Value("${partition2.datasource.username}")
    private String p2PrimaryUsername;
    @Value("${partition2.datasource.password}")
    private String p2PrimaryPassword;

    @Value("${partition2.fallback.datasource.url}")
    private String p2FallbackUrl;
    @Value("${partition2.fallback.datasource.username}")
    private String p2FallbackUsername;
    @Value("${partition2.fallback.datasource.password}")
    private String p2FallbackPassword;

    @Primary
    @Bean(name = "counterDataSource")
    public DataSource counterDataSource() {
        return DataSourceBuilder.create()
                .url(counterUrl)
                .username(counterUsername)
                .password(counterPassword)
                .build();
    }

    @Bean(name = "counterJdbcTemplate")
    public JdbcTemplate counterJdbcTemplate(@Qualifier("counterDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "partition0PrimaryDataSource")
    public DataSource partition0PrimaryDataSource() {
        return DataSourceBuilder.create().url(p0PrimaryUrl).username(p0PrimaryUsername).password(p0PrimaryPassword).build();
    }

    @Bean(name = "partition0FallbackDataSource")
    public DataSource partition0FallbackDataSource() {
        return DataSourceBuilder.create().url(p0FallbackUrl).username(p0FallbackUsername).password(p0FallbackPassword).build();
    }

    @Bean(name = "partition0PrimaryJdbcTemplate")
    public JdbcTemplate partition0PrimaryJdbcTemplate(@Qualifier("partition0PrimaryDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "partition0FallbackJdbcTemplate")
    public JdbcTemplate partition0FallbackJdbcTemplate(@Qualifier("partition0FallbackDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "partition1PrimaryDataSource")
    public DataSource partition1PrimaryDataSource() {
        return DataSourceBuilder.create().url(p1PrimaryUrl).username(p1PrimaryUsername).password(p1PrimaryPassword).build();
    }

    @Bean(name = "partition1FallbackDataSource")
    public DataSource partition1FallbackDataSource() {
        return DataSourceBuilder.create().url(p1FallbackUrl).username(p1FallbackUsername).password(p1FallbackPassword).build();
    }

    @Bean(name = "partition1PrimaryJdbcTemplate")
    public JdbcTemplate partition1PrimaryJdbcTemplate(@Qualifier("partition1PrimaryDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "partition1FallbackJdbcTemplate")
    public JdbcTemplate partition1FallbackJdbcTemplate(@Qualifier("partition1FallbackDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "partition2PrimaryDataSource")
    public DataSource partition2PrimaryDataSource() {
        return DataSourceBuilder.create().url(p2PrimaryUrl).username(p2PrimaryUsername).password(p2PrimaryPassword).build();
    }

    @Bean(name = "partition2FallbackDataSource")
    public DataSource partition2FallbackDataSource() {
        return DataSourceBuilder.create().url(p2FallbackUrl).username(p2FallbackUsername).password(p2FallbackPassword).build();
    }

    @Bean(name = "partition2PrimaryJdbcTemplate")
    public JdbcTemplate partition2PrimaryJdbcTemplate(@Qualifier("partition2PrimaryDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "partition2FallbackJdbcTemplate")
    public JdbcTemplate partition2FallbackJdbcTemplate(@Qualifier("partition2FallbackDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
