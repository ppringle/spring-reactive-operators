package com.pringweb.r2dbc.springdata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = {DBIntegrationTest.Initializer.class})
@Slf4j
public abstract class DBIntegrationTest {

    public static PostgreSQLContainer<?> postgreDBContainer =
            new PostgreSQLContainer<>("postgres:14-alpine")
                    .withUsername("sa")
                    .withPassword("sa")
                    .withDatabaseName("orders");

    static {
        postgreDBContainer.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            String r2dbcUrl =
                    String.format("r2dbc:postgresql://%s:%s/orders", postgreDBContainer.getHost(),
                            postgreDBContainer.getFirstMappedPort());

            log.info("r2dbcUrl: {}", r2dbcUrl);

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.r2dbc.url=" + r2dbcUrl,
                    "spring.r2dbc.username=" + postgreDBContainer.getUsername(),
                    "spring.r2dbc.password=" + postgreDBContainer.getPassword()
            );
        }

    }

}
