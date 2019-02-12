package com.imc.rps.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class loading up mongodb application properties.
 */
@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class AppConfig {

    private String host;
    private String port;
    private String database;
    private String uri;

}
