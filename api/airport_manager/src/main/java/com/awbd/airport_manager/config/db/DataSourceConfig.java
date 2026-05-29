package com.awbd.airport_manager.config.db;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "projectDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.airport-manager")
    public HikariDataSource dataSource() {
        return new HikariDataSource();
    }

    @Primary
    @Bean(name = "projectJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.airport-manager")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean
    public SpringLiquibase projectLiquibase(@Qualifier("projectDataSource") HikariDataSource projectDataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(projectDataSource);
        liquibase.setChangeLog("classpath:/liquibase/changelog.xml");
        return liquibase;
    }
}
