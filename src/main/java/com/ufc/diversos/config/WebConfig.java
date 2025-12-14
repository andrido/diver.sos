package com.ufc.diversos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:5173",  // frontend em desenvolvimento (Vite)
                        "https://seufrontend.com" // frontend em produção (IMPORTANTE: SUBSTITUIR!)
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Os métodos que sua API usa
                .allowCredentials(true) // Necessário se você usar cookies ou sessões
                .maxAge(3600); // Cache do resultado do "preflight"
    }
}