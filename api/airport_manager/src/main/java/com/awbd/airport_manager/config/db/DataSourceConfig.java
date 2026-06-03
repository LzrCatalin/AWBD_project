package com.awbd.airport_manager.config.db;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "projectJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.airport-manager")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }
}
