package com.stalwart.customer;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public abstract class AbstractFirstTest {


    protected static final Faker faker = new Faker();

    @BeforeAll
    static void beforeAll() {
        Flyway flyway = Flyway.configure().dataSource(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        ).load();
        flyway.migrate();
    }

    @Container
    protected static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("stalwart-dao-unit-test")
                    .withUsername("pavan")
                    .withPassword("postgres")
                    .withCommand("postgres", "-c", "max_connections=200");
    @DynamicPropertySource
    private static void registerDataSourceProps(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",
                postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username",
                postgresContainer::getUsername);
        registry.add("spring.datasource.password",
                postgresContainer::getPassword);
    }

    private final DataSource getDataSource(){
        return DataSourceBuilder.create()
                .driverClassName(postgresContainer.getDriverClassName())
                .url(postgresContainer.getJdbcUrl())
                .username(postgresContainer.getUsername())
                .password(postgresContainer.getPassword()).build();
    }

    protected final JdbcTemplate getJDBCTemplate(){
        return new JdbcTemplate(getDataSource());
    }

}
