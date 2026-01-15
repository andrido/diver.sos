package com.ufc.diversos.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import jakarta.annotation.PostConstruct;
import java.util.stream.Collectors;

@Configuration
public class DotenvConfig {

    private final ConfigurableEnvironment env;

    public DotenvConfig(ConfigurableEnvironment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        env.getPropertySources().addFirst(new MapPropertySource("dotenvProperties",
                dotenv.entries().stream()
                        .collect(Collectors.toMap(DotenvEntry::getKey, DotenvEntry::getValue))));
    }
}