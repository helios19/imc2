package com.imc.rps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Main Spring Boot Application class running the RPS game.
 */
@EnableMongoRepositories(basePackages = "com.imc.rps.game.repository")
@SpringBootApplication
public class Application {

    public static final void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

}
