package com.votingapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Vote {
    // Equipo votado ("america" o "chivas")
    private String team;
    // Identificador único del nodo/votante
    private String nodeId;
    // Fecha y hora del voto
    private LocalDateTime timestamp;

    // Constructor vacío necesario para Jackson
    public Vote() {}

    // Constructor con parámetros
    public Vote(String team, String nodeId) {
        this.team = team;
        this.nodeId = nodeId;
        // Timestamp automático
        this.timestamp = LocalDateTime.now();
    }

    // Getters y Setters
    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}