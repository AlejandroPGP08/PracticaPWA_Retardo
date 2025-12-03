package com.votingapp; // Paquete base de la aplicación

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Anotación que marca esta clase como aplicación Spring Boot
@SpringBootApplication
public class VotingAppApplication {
    public static void main(String[] args) {
        // Inicia la aplicación Spring Boot
        SpringApplication.run(VotingAppApplication.class, args);
    }
}