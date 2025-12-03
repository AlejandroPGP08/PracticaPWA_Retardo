package com.votingapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Marca esta clase como configuración de Spring
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Aplica CORS a todos los endpoints /api
        registry.addMapping("/api/**")
                // Permite cualquier origen
                .allowedOrigins("*")
                // Métodos permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // Todos los headers permitidos
                .allowedHeaders("*");
    }
}