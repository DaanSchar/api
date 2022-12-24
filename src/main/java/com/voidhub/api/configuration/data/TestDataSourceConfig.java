package com.voidhub.api.configuration.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("test")
public class TestDataSourceConfig {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean
    public DataSource dataSource() {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

}