package com.example.externalconfig;

import com.example.externalconfig.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class ExternalConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalConfigApplication.class, args);
    }
}
