package com.github.hippoom.springtestdbunittemplate.sample;

import org.flywaydb.core.Flyway;
import org.flywaydb.test.junit.FlywayHelperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean
    @Autowired
    public Flyway flyway(DataSource dataSource) {
        final Flyway flyway = new FlywayHelperFactory().createFlyway();
        flyway.setDataSource(dataSource);
        return flyway;
    }

}
