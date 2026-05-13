package io.github.clientlibrary.client_library.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("io.github.clientlibrary.client_library")
public class ConfigClientAutoConfiguration {

    @PostConstruct
    public void init() {
        System.out.println("Client library initialized.");
    }
}
